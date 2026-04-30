package com.springnotify.repository;

import com.springnotify.model.NotificationEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<NotificationEvent, UUID> {
    Optional<NotificationEvent> findByIdempotencyKey(String idempotencyKey);
}
