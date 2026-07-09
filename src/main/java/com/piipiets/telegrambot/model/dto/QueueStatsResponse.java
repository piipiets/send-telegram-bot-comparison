package com.piipiets.telegrambot.model.dto;

public record QueueStatsResponse(
        int capacity,
        int queued,
        int remainingCapacity,
        int workers,
        long sentSuccessfully,
        long failed
) {
}
