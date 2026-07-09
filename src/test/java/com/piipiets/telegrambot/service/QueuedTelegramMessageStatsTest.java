package com.piipiets.telegrambot.service;

import com.piipiets.telegrambot.config.properties.TelegramQueueProperties;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class QueuedTelegramMessageStatsTest {

    @Test
    void exposesSuccessfulAndFailedSendCounts() {
        QueuedTelegramMessageService service = new QueuedTelegramMessageService(
                new TelegramQueueProperties(10, 2, 1),
                mock(TelegramMessageSender.class)
        );

        ReflectionTestUtils.setField(service, "sentSuccessfully", new AtomicLong(7));
        ReflectionTestUtils.setField(service, "failed", new AtomicLong(3));

        var stats = service.stats();

        assertThat(stats.sentSuccessfully()).isEqualTo(7);
        assertThat(stats.failed()).isEqualTo(3);
    }
}
