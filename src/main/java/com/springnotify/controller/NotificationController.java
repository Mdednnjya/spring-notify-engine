package com.springnotify.controller;

import com.springnotify.model.NotificationEvent;
import com.springnotify.model.NotificationRequest;
import com.springnotify.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<NotificationEvent> submit(@RequestBody NotificationRequest req) {
        return ResponseEntity.ok(service.submit(req));
    }
}
