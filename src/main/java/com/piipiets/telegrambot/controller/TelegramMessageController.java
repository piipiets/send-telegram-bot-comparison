package com.piipiets.telegrambot.controller;

import com.piipiets.telegrambot.model.dto.MessageRequest;
import com.piipiets.telegrambot.model.dto.MessageResponse;
import com.piipiets.telegrambot.model.dto.QueueStatsResponse;
import com.piipiets.telegrambot.model.response.DataResponse;
import com.piipiets.telegrambot.service.DirectTelegramMessageService;
import com.piipiets.telegrambot.service.QueuedTelegramMessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/messages")
public class TelegramMessageController {

    private final QueuedTelegramMessageService queuedService;
    private final DirectTelegramMessageService directService;

    public TelegramMessageController(QueuedTelegramMessageService queuedService,
                                     DirectTelegramMessageService directService) {
        this.queuedService = queuedService;
        this.directService = directService;
    }

    @PostMapping("/queued")
    public ResponseEntity<DataResponse<MessageResponse>> sendQueued(@Valid @RequestBody MessageRequest request) {
        return queuedService.enqueue(request);
    }

    @PostMapping("/direct")
    public ResponseEntity<DataResponse<MessageResponse>> sendDirect(@Valid @RequestBody MessageRequest request) {
        return directService.send(request);
    }

    @GetMapping("/queued/stats")
    public QueueStatsResponse queueStats() {
        return queuedService.stats();
    }
}
