package ru.valera.my_own.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import ru.valera.my_own.enums.TaskStatus;

@Getter
public class TaskRequest {

    @NotBlank(message = "title is required")
    private String title;

    private String description;

    @NotNull
    private TaskStatus status;

    @NotNull(message = "category id is required")
    private Long categoryId;

    @NotNull(message = "user id is required")
    private Long userId;

    public TaskRequest(){}

}
