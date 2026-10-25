package ru.valera.my_own.dto;

import lombok.Getter;
import ru.valera.my_own.enums.TaskStatus;

@Getter
public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private Long categoryId;
    private Long userId;

    public TaskResponse(Long id, String title, String description, TaskStatus status, Long categoryId, Long userId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.categoryId = categoryId;
        this.userId = userId;
    }

    public TaskResponse() {
    }
}
