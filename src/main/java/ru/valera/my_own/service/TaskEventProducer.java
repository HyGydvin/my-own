package ru.valera.my_own.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.valera.my_own.event.TaskCreatedEvent;

@Slf4j
@RequiredArgsConstructor
@Component
public class TaskEventProducer {

    private static final String TOPIC = "task-events";

    private final KafkaTemplate<String, TaskCreatedEvent> kafkaTemplate;

    public void sendTaskCreatedEvent(TaskCreatedEvent event){
        kafkaTemplate.send(TOPIC, event.taskId().toString(), event)
                .whenComplete((result, exception) -> {
                    if (exception != null){
                        log.error(
                                "Failed to send task-created event: taskId={}",
                                event.taskId(),
                                exception
                        );
                    } else {
                        log.info(
                                "Task-created event sent: taskId={}, partition={}",
                                event.taskId(),
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition()
                        );
                    }
                });
    }

}
