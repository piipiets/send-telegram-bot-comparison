package com.piipiets.telegrambot.service;

import com.piipiets.telegrambot.config.properties.TelegramProperties;
import com.piipiets.telegrambot.exception.TelegramApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.JsonNode;

import java.util.Map;
import java.util.HashMap;

@Component
@Slf4j
public class TelegramApiClient {

    private final TelegramProperties properties;
    private final RestTemplate restTemplate;

    public TelegramApiClient(TelegramProperties properties,
                             RestTemplate telegramRestTemplate) {
        this.properties = properties;
        this.restTemplate = telegramRestTemplate;
    }

    public int sendMessage(String chatId, String text) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> body = new HashMap<>();
        body.put("chat_id", chatId);
        body.put("text", text);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            JsonNode response = restTemplate.postForObject(properties.sendMessageUrl(), request, JsonNode.class);
            if (response == null || !response.path("ok").asBoolean(false)) {
                throw new TelegramApiException(502, "Telegram returned an empty or unsuccessful response");
            }

            return response.path("result").path("message_id").asInt();
        } catch (Exception ex) {
            log.error("Error when hit telegram api", ex);
            throw ex;
        }
    }
}
