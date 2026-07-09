package com.piipiets.telegrambot.exception;

public class TelegramApiException extends RuntimeException {

    private final int statusCode;

    public TelegramApiException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int statusCode() {
        return statusCode;
    }
}
