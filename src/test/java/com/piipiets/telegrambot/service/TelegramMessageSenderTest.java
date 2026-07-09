package com.piipiets.telegrambot.service;

import com.piipiets.telegrambot.config.properties.TelegramProperties;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TelegramMessageSenderTest {

    @Test
    void stopsAfterFirstSuccessfulSend() {
        TelegramApiClient client = mock(TelegramApiClient.class);
        when(client.sendMessage("123", "hello")).thenReturn(42);

        TelegramMessageSender sender = new TelegramMessageSender(
                new TelegramProperties("token", null, 3, 1_000L),
                client
        );

        assertThatCode(() -> sender.send("123", "hello")).doesNotThrowAnyException();

        verify(client).sendMessage("123", "hello");
    }
}
