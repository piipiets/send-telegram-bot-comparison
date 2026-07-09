package com.piipiets.telegrambot.service;

import com.piipiets.telegrambot.config.properties.TelegramProperties;
import com.piipiets.telegrambot.exception.TelegramRateLimitException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Slf4j
public class TelegramMessageSender {

    private final TelegramProperties properties;
    private final TelegramApiClient telegramApiClient;
    private final ConcurrentHashMap<String, Long> lastSentPerChat = new ConcurrentHashMap<>();
    private final AtomicLong lastGlobalSent = new AtomicLong(0);
    private static final long CHAT_DELAY_MS = 5000; // 5 sec
    private static final long GLOBAL_DELAY_MS = 34; // 1s / 30 msg = ~33.3ms


    public TelegramMessageSender(TelegramProperties properties,
                                 TelegramApiClient telegramApiClient) {
        this.properties = properties;
        this.telegramApiClient = telegramApiClient;
    }

    public void send(String chatId, String text) {
        applyThrottling(chatId);
        RuntimeException lastFailure = null;
        for (int attempt = 1; attempt <= properties.maxSendAttempts(); attempt++) {
            try {
                log.info("ATTEMPT {}", attempt);
                telegramApiClient.sendMessage(chatId, text);
                return;
            } catch (TelegramRateLimitException ex) {
                log.error("Telegram API RATE LIMIT EXCEEDED", ex);
                lastFailure = ex;
                sleepBeforeRetry(ex.retryAfterMillis(), attempt);
            } catch (RuntimeException ex) {
                lastFailure = ex;
                sleepBeforeRetry(properties.defaultRetryAfterMillis() * attempt, attempt);
            }
        }

        log.error("Telegram send failed after {} attempts", properties.maxSendAttempts(), lastFailure);
        throw lastFailure == null ? new IllegalStateException("Telegram send failed") : lastFailure;
    }

    private void sleepBeforeRetry(long delayMillis, int attempt) {
        log.info("sleep for : {}", delayMillis);
        if (attempt >= properties.maxSendAttempts()) {
            return;
        }

        try {
            Thread.sleep(delayMillis);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while backing off Telegram send", ex);
        }
    }

    private void applyThrottling(String chatId) { //memperlambat kinerja
        long now = System.currentTimeMillis();

        // 1. Global Throttling (30 msg/sec)
        synchronized (lastGlobalSent) {
            long waitGlobal = (lastGlobalSent.get() + GLOBAL_DELAY_MS) - now;
            if (waitGlobal > 0) {
                sleepSilent(waitGlobal);
                now = System.currentTimeMillis();
            }
            lastGlobalSent.set(now);
        }

        // 2. Per-Chat Throttling (20 msg/min)
        Long lastChatTime = lastSentPerChat.get(chatId);
        if (lastChatTime != null) {
            long waitChat = (lastChatTime + CHAT_DELAY_MS) - now;
            if (waitChat > 0) {
                log.info("Throttling chat {}: waiting {}ms", chatId, waitChat);
                sleepSilent(waitChat);
                now = System.currentTimeMillis();
            }
        }
        lastSentPerChat.put(chatId, now);
    }

    private void sleepSilent(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
