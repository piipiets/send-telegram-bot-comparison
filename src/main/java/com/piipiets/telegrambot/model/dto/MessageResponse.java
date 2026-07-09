package com.piipiets.telegrambot.model.dto;

import com.piipiets.telegrambot.model.MessageStatus;
import com.piipiets.telegrambot.model.SendMode;

public record MessageResponse(
        String chatId,
        SendMode mode,
        MessageStatus status,
        Integer telegramMessageId
) {
}
