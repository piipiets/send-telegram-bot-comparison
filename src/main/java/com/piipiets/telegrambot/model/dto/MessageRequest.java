package com.piipiets.telegrambot.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MessageRequest(
        @NotBlank String chatId,
        @NotBlank @Size(max = 4096) String text
) {
}
