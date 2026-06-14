package com.springnotify.dispatcher;

import org.springframework.stereotype.Component;

@Component
public class MockDispatcher {

    public void dispatch(String recipient, String type, String payload) {
        try {
            // simulate SMTP latency — runs on worker thread, not request thread
            Thread.sleep(2000);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("dispatch interrupted: " + e.getMessage(), e);
        }
    }
}
