package com.piipiets.telegrambot;

import com.piipiets.telegrambot.config.properties.TelegramProperties;
import com.piipiets.telegrambot.config.properties.TelegramQueueProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({TelegramProperties.class, TelegramQueueProperties.class})
public class TelegramBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(TelegramBotApplication.class, args);
    }
}
