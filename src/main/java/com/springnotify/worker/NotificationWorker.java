package com.springnotify.worker;

import com.springnotify.dispatcher.MockDispatcher;
import com.springnotify.model.NotificationEvent;
import com.springnotify.model.NotificationStatus;
import com.springnotify.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;

@Component
public class NotificationWorker {

    private static final String QUEUE_KEY = "notification:queue";

    private final NotificationRepository repo;
    private final MockDispatcher         dispatcher;
    private final StringRedisTemplate    redis;
    private final int                    maxRetry;

    public NotificationWorker(NotificationRepository repo, MockDispatcher dispatcher,
                              StringRedisTemplate redis,
                              @Value("${notification.max-retry}") int maxRetry) {
        this.repo       = repo;
        this.dispatcher = dispatcher;
        this.redis      = redis;
        this.maxRetry   = maxRetry;
    }

    @Scheduled(fixedDelay = 1000)
    public void processNext() {
        // poll
        String eventId = redis.opsForList().leftPop(QUEUE_KEY, Duration.ofSeconds(5));
        if (eventId == null) return;

        // fetch
        NotificationEvent event = repo.findById(UUID.fromString(eventId)).orElse(null);
        if (event == null) return;

        // idempotency check
        if (event.getStatus() == NotificationStatus.DELIVERED) return;

        // processing
        event.setStatus(NotificationStatus.PROCESSING);
        repo.save(event);

        try {
            dispatcher.dispatch(event.getRecipient(), event.getType().name(), event.getPayload());
            // state update
            event.setStatus(NotificationStatus.DELIVERED);
            repo.save(event);
        } catch (Exception e) {
            event.setRetryCount(event.getRetryCount() + 1);
            if (event.getRetryCount() >= maxRetry) {
                // dead letter
                event.setStatus(NotificationStatus.FAILED);
                repo.save(event);
            } else {
                // re-enqueue
                event.setStatus(NotificationStatus.PENDING);
                repo.save(event);
                redis.opsForList().rightPush(QUEUE_KEY, eventId);
            }
        }
    }
}
