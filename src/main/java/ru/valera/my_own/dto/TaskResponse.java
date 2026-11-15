package ru.valera.my_own.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.valera.my_own.enums.TaskStatus;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private LocalDateTime createdAt;
    private Long categoryId;
    private Long userId;

    public TaskResponse() {
    }
}
