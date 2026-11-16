
package ru.valera.my_own.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class OutboxPublisher {

    private final OutboxBatchProcessor outboxBatchProcessor;

    @Scheduled(fixedDelay = 5000)
    public void publishEvents() {
        outboxBatchProcessor.publishBatch();
    }
}
