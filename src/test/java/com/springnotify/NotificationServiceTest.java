package com.springnotify;

import com.springnotify.dispatcher.MockDispatcher;
import com.springnotify.model.NotificationEvent;
import com.springnotify.model.NotificationRequest;
import com.springnotify.model.NotificationStatus;
import com.springnotify.model.NotificationType;
import com.springnotify.repository.NotificationRepository;
import com.springnotify.service.NotificationServiceImpl;
import com.springnotify.worker.NotificationWorker;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    @Test
    void submit_duplicateKey_returnsExistingWithoutSave() {
        NotificationRepository repo       = mock(NotificationRepository.class);
        StringRedisTemplate    redis      = mock(StringRedisTemplate.class);

        NotificationEvent existing = new NotificationEvent();
        existing.setIdempotencyKey("key-001");
        existing.setStatus(NotificationStatus.DELIVERED);

        when(repo.findByIdempotencyKey("key-001")).thenReturn(Optional.of(existing));

        NotificationServiceImpl service = new NotificationServiceImpl(repo, redis);

        NotificationRequest req = new NotificationRequest();
        req.setIdempotencyKey("key-001");
        req.setRecipient("user@example.com");
        req.setType(NotificationType.BOOKING_CONFIRMATION);
        req.setPayload("test");

        NotificationEvent result = service.submit(req);

        assertSame(existing, result);
        verify(repo, never()).save(any());
        verify(redis, never()).opsForList();
    }

    @Test
    @SuppressWarnings("unchecked")
    void worker_processNext_transitionsPendingToDelivered() {
        NotificationRepository            repo       = mock(NotificationRepository.class);
        MockDispatcher                    dispatcher = mock(MockDispatcher.class);
        StringRedisTemplate               redis      = mock(StringRedisTemplate.class);
        ListOperations<String, String>    listOps    = mock(ListOperations.class);

        UUID              id    = UUID.randomUUID();
        NotificationEvent event = new NotificationEvent();
        event.setId(id);
        event.setRecipient("user@example.com");
        event.setType(NotificationType.BOOKING_CONFIRMATION);
        event.setPayload("test");
        event.setStatus(NotificationStatus.PENDING);
        event.setRetryCount(0);

        when(redis.opsForList()).thenReturn(listOps);
        when(listOps.leftPop(anyString(), any(Duration.class))).thenReturn(id.toString());
        when(repo.findById(id)).thenReturn(Optional.of(event));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        NotificationWorker worker = new NotificationWorker(repo, dispatcher, redis, 3);
        worker.processNext();

        assertEquals(NotificationStatus.DELIVERED, event.getStatus());
        verify(dispatcher).dispatch(anyString(), anyString(), anyString());
    }

    @Test
    @SuppressWarnings("unchecked")
    void worker_processNext_deadLetterWhenMaxRetryReached() {
        NotificationRepository            repo       = mock(NotificationRepository.class);
        MockDispatcher                    dispatcher = mock(MockDispatcher.class);
        StringRedisTemplate               redis      = mock(StringRedisTemplate.class);
        ListOperations<String, String>    listOps    = mock(ListOperations.class);

        UUID              id    = UUID.randomUUID();
        NotificationEvent event = new NotificationEvent();
        event.setId(id);
        event.setRecipient("user@example.com");
        event.setType(NotificationType.BOOKING_CONFIRMATION);
        event.setPayload("test");
        event.setStatus(NotificationStatus.PENDING);
        event.setRetryCount(2);

        when(redis.opsForList()).thenReturn(listOps);
        when(listOps.leftPop(anyString(), any(Duration.class))).thenReturn(id.toString());
        when(repo.findById(id)).thenReturn(Optional.of(event));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        doThrow(new RuntimeException("dispatch failed"))
                .when(dispatcher).dispatch(anyString(), anyString(), anyString());

        NotificationWorker worker = new NotificationWorker(repo, dispatcher, redis, 3);
        worker.processNext();

        assertEquals(NotificationStatus.FAILED, event.getStatus());
        assertEquals(3, event.getRetryCount());
        verify(listOps, never()).rightPush(anyString(), anyString());
    }
}
