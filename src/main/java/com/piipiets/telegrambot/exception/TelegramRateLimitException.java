package com.piipiets.telegrambot.exception;

public class TelegramRateLimitException extends TelegramApiException {

    private final long retryAfterMillis;

    public TelegramRateLimitException(long retryAfterMillis, String message) {
        super(429, message);
        this.retryAfterMillis = retryAfterMillis;
    }

    public long retryAfterMillis() {
        return retryAfterMillis;
    }
}
