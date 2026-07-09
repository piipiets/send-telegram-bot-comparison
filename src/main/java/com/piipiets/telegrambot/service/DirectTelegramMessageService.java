package com.piipiets.telegrambot.service;

import com.piipiets.telegrambot.model.dto.MessageRequest;
import com.piipiets.telegrambot.model.dto.MessageResponse;
import com.piipiets.telegrambot.model.MessageStatus;
import com.piipiets.telegrambot.model.SendMode;
import com.piipiets.telegrambot.model.response.DataResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class DirectTelegramMessageService {

    private final TelegramApiClient telegramApiClient;

    public DirectTelegramMessageService(TelegramApiClient telegramApiClient) {
        this.telegramApiClient = telegramApiClient;
    }

    public ResponseEntity<DataResponse<MessageResponse>> send(MessageRequest request) {
        try {
            int messageId = telegramApiClient.sendMessage(request.chatId(), request.text());
            MessageResponse res = new MessageResponse(
                    request.chatId(),
                    SendMode.DIRECT,
                    MessageStatus.SENT,
                    messageId);

            return ResponseEntity.ok(new DataResponse<>("OK", "Successfully send message", new Date(), 201, res));
        } catch (Exception e) {
            log.error("Error sending direct telegram message", e);
            throw e;
        }
    }
}
