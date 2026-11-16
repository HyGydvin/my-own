
package ru.valera.my_own.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import ru.valera.my_own.event.TaskCreatedEvent;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Component
public class TaskEventProducer {

    private static final String TOPIC = "task-events";

    private final KafkaTemplate<String, TaskCreatedEvent> kafkaTemplate;

    public CompletableFuture<SendResult<String, TaskCreatedEvent>> sendTaskCreatedEvent(
            TaskCreatedEvent event
    ) {
        return kafkaTemplate.send(
                TOPIC,
                event.taskId().toString(),
                event
        );
    }
}
