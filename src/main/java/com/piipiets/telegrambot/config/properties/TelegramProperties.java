package com.piipiets.telegrambot.config.properties;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "telegram")
public record TelegramProperties(
        @NotBlank String botToken,
        String apiBaseUrl,
        int maxSendAttempts,
        long defaultRetryAfterMillis
) {
    public TelegramProperties {
        if (apiBaseUrl == null || apiBaseUrl.isBlank()) {
            apiBaseUrl = "https://api.telegram.org";
        }
        if (maxSendAttempts <= 0) {
            maxSendAttempts = 4;
        }
        if (defaultRetryAfterMillis <= 0) {
            defaultRetryAfterMillis = 1_000L;
        }
    }

    public String sendMessageUrl() {
        return "%s/bot%s/sendMessage".formatted(apiBaseUrl, botToken);
    }
}
