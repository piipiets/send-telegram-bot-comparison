package com.piipiets.telegrambot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpStatusCodeException;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(QueueCapacityExceededException.class)
    ProblemDetail handleQueueFull(QueueCapacityExceededException ex) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.TOO_MANY_REQUESTS);
        detail.setTitle("Queue capacity exceeded");
        detail.setDetail(ex.getMessage());
        return detail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setTitle("Invalid message request");
        detail.setDetail(ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .orElse("Request validation failed"));
        return detail;
    }

    @ExceptionHandler(HttpStatusCodeException.class)
    ProblemDetail handleTelegramError(HttpStatusCodeException ex) {
        if (ex.getStatusCode().value() == 429) {
            // For direct endpoint, don't parse retry time or include retry-related info
            ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.TOO_MANY_REQUESTS);
            detail.setTitle("Telegram rate limit exceeded");
            detail.setDetail("Rate limit exceeded. Please try again later.");
            return detail;
        }

        ProblemDetail detail = ProblemDetail.forStatus(ex.getStatusCode());
        detail.setTitle("Telegram API Error");
        detail.setDetail(ex.getResponseBodyAsString().isBlank() ? ex.getMessage() : ex.getResponseBodyAsString());
        return detail;
    }
}
