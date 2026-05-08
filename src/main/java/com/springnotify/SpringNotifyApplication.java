package com.springnotify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpringNotifyApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringNotifyApplication.class, args);
    }
}
