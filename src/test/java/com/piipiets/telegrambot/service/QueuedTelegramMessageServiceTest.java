package com.piipiets.telegrambot.service;

import com.piipiets.telegrambot.config.properties.TelegramQueueProperties;
import com.piipiets.telegrambot.model.dto.MessageRequest;
import com.piipiets.telegrambot.exception.QueueCapacityExceededException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class QueuedTelegramMessageServiceTest {

    @Test
    void rejectsWhenQueueIsFull() {
        TelegramMessageSender sender = mock(TelegramMessageSender.class);

        QueuedTelegramMessageService service = new QueuedTelegramMessageService(
                new TelegramQueueProperties(1, 0, 1),
                sender
        );

        service.enqueue(new MessageRequest("1", "first"));

        assertThatThrownBy(() -> service.enqueue(new MessageRequest("1", "second")))
                .isInstanceOf(QueueCapacityExceededException.class);
    }
}
