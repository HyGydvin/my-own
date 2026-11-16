
package ru.valera.my_own.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.valera.my_own.entity.OutboxEvent;
import ru.valera.my_own.event.TaskCreatedEvent;
import ru.valera.my_own.repository.OutboxEventRepository;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class OutboxBatchProcessor {

    private static final int BATCH_SIZE = 3;

    private final OutboxEventRepository outboxEventRepository;
    private final TaskEventProducer taskEventProducer;
    private final ObjectMapper objectMapper;

    @Transactional
    public void publishBatch() {
        List<OutboxEvent> events =
                outboxEventRepository.findUnpublishedEventsForUpdate(BATCH_SIZE);

        log.info("Found {} unpublished outbox events", events.size());

        for (OutboxEvent event : events) {
            try {
                publishEvent(event);
            } catch (Exception e) {
                log.error(
                        "Failed to publish outbox event: eventId={}",
                        event.getId(),
                        e
                );
            }
        }
    }

    private void publishEvent(OutboxEvent event) {
        if (!"TASK_CREATED".equals(event.getEventType())) {
            log.warn(
                    "Unknown outbox event type: eventId={}, type={}",
                    event.getId(),
                    event.getEventType()
            );
            return;
        }

        TaskCreatedEvent taskCreatedEvent = objectMapper.readValue(
                event.getPayload(),
                TaskCreatedEvent.class
        );

        taskEventProducer.sendTaskCreatedEvent(taskCreatedEvent).join();

        event.setPublishedAt(LocalDateTime.now());

        log.info("Outbox event published: eventId={}", event.getId());
    }
}
