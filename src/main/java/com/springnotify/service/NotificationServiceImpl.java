package com.springnotify.service;

import com.springnotify.model.NotificationEvent;
import com.springnotify.model.NotificationRequest;
import com.springnotify.model.NotificationStatus;
import com.springnotify.repository.NotificationRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final String QUEUE_KEY = "notification:queue";

    private final NotificationRepository repo;
    private final StringRedisTemplate    redis;

    public NotificationServiceImpl(NotificationRepository repo, StringRedisTemplate redis) {
        this.repo  = repo;
        this.redis = redis;
    }

    @Override
    public NotificationEvent submit(NotificationRequest req) {

        // guard
        if (req.getIdempotencyKey() == null || req.getIdempotencyKey().isBlank()) {
            throw new IllegalArgumentException("idempotencyKey required");
        }
        
        if (req.getRecipient() == null || req.getRecipient().isBlank()) {
            throw new IllegalArgumentException("recipient cannot be empty");
        }

        
        if (req.getType() == null) {
            throw new IllegalArgumentException("type required");
        }

        // cache hit
        Optional<NotificationEvent> existing = repo.findByIdempotencyKey(req.getIdempotencyKey());
        
        if (existing.isPresent()) return existing.get();

        // save
        NotificationEvent event = new NotificationEvent();
        event.setIdempotencyKey(req.getIdempotencyKey());
        event.setRecipient(req.getRecipient());
        event.setType(req.getType());
        event.setPayload(req.getPayload());
        event.setStatus(NotificationStatus.PENDING);
        event.setRetryCount(0);
        repo.save(event);

        // enqueue
        redis.opsForList().rightPush(QUEUE_KEY, event.getId().toString());

        return event;
    }
}
