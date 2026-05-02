package com.springnotify.service;

import com.springnotify.model.NotificationEvent;
import com.springnotify.model.NotificationRequest;

public interface NotificationService {
    NotificationEvent submit(NotificationRequest req);
}
