package com.piipiets.telegrambot.exception;

public class QueueCapacityExceededException extends RuntimeException {

    public QueueCapacityExceededException(String message) {
        super(message);
    }
}
