package com.piipiets.telegrambot.service;

import com.piipiets.telegrambot.config.properties.TelegramQueueProperties;
import com.piipiets.telegrambot.model.dto.MessageRequest;
import com.piipiets.telegrambot.model.dto.MessageResponse;
import com.piipiets.telegrambot.model.dto.QueueStatsResponse;
import com.piipiets.telegrambot.exception.QueueCapacityExceededException;
import com.piipiets.telegrambot.model.MessageStatus;
import com.piipiets.telegrambot.model.SendMode;
import com.piipiets.telegrambot.model.dto.QueuedMessageTask;
import com.piipiets.telegrambot.model.response.DataResponse;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Slf4j
public class QueuedTelegramMessageService {

    private final TelegramQueueProperties properties;
    private final TelegramMessageSender sender;
    private final BlockingQueue<QueuedMessageTask> queue;
    private final AtomicLong sentSuccessfully = new AtomicLong();
    private final AtomicLong failed = new AtomicLong();
    private ExecutorService workers;

    public QueuedTelegramMessageService(TelegramQueueProperties properties,
                                        TelegramMessageSender sender) {
        this.properties = properties;
        this.sender = sender;
        this.queue = new ArrayBlockingQueue<>(properties.capacity());
    }

    @PostConstruct
    void startWorkers() {
        workers = Executors.newFixedThreadPool(properties.workers());
        for (int i = 0; i < properties.workers(); i++) {
            workers.submit(this::drainQueue);
        }
    }

    @PreDestroy
    void stopWorkers() {
        if (workers != null) {
            workers.shutdownNow();
        }
    }

    public ResponseEntity<DataResponse<MessageResponse>> enqueue(MessageRequest request) {
        try {
            boolean offered = queue.offer(new QueuedMessageTask(request.chatId(), request.text()),
                                properties.enqueueTimeoutMillis(),
                                TimeUnit.MILLISECONDS);

            if (!offered) {
                throw new QueueCapacityExceededException("Message queue is full; retry later");
            }

            MessageResponse res = new MessageResponse(request.chatId(), SendMode.QUEUED, MessageStatus.PENDING, null);

            return ResponseEntity.ok(new DataResponse<>("Success", "Success adding message to queue", new Date(), 202, res));
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt(); //telling thread to stop
            throw new IllegalStateException("Interrupted while waiting for queue capacity", ex);
        }
    }

    public QueueStatsResponse stats() {
        return new QueueStatsResponse(
                properties.capacity(),
                queue.size(),
                queue.remainingCapacity(),
                properties.workers(),
                sentSuccessfully.get(),
                failed.get()
        );
    }

    private void drainQueue() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                QueuedMessageTask task = queue.take();
                sender.send(task.chatId(), task.text());
                sentSuccessfully.incrementAndGet();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                return; // Exit worker
            } catch (Exception ex) {
                failed.incrementAndGet();
                log.error("Failed to send queued Telegram message", ex);
            }
        }
    }
}
