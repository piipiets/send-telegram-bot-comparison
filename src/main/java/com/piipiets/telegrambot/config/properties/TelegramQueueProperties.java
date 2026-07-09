package com.piipiets.telegrambot.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "telegram.queue")
public record TelegramQueueProperties(
        int capacity,
        int workers,
        long enqueueTimeoutMillis
) {
    public TelegramQueueProperties {
        if (capacity <= 0) {
            capacity = 1_000;
        }

        if (workers <= 0) {
            workers = 2;
        }
        if (enqueueTimeoutMillis <= 0) {
            enqueueTimeoutMillis = 250L;
        }
    }
}
