package com.springnotify.service;

import com.springnotify.dispatcher.MockDispatcher;
import com.springnotify.model.NotificationEvent;
import com.springnotify.model.NotificationRequest;
import com.springnotify.model.NotificationStatus;
import com.springnotify.repository.NotificationRepository;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository repo;
    private final MockDispatcher         dispatcher;

    public NotificationServiceImpl(NotificationRepository repo, MockDispatcher dispatcher) {
        this.repo       = repo;
        this.dispatcher = dispatcher;
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

        // save
        NotificationEvent event = new NotificationEvent();
        event.setIdempotencyKey(req.getIdempotencyKey());
        event.setRecipient(req.getRecipient());
        event.setType(req.getType());
        event.setPayload(req.getPayload());
        event.setStatus(NotificationStatus.PENDING);
        event.setRetryCount(0);
        repo.save(event);

        // dispatch — blocks caller thread
        dispatcher.dispatch(event.getRecipient(), event.getType().name(), event.getPayload());

        // state update
        event.setStatus(NotificationStatus.DELIVERED);
        repo.save(event);

        return event;
    }
}
