package ru.valera.my_own.mapper;

import org.springframework.stereotype.Component;
import ru.valera.my_own.dto.TaskRequest;
import ru.valera.my_own.dto.TaskResponse;
import ru.valera.my_own.entity.Category;
import ru.valera.my_own.entity.Task;
import ru.valera.my_own.entity.User;

@Component
public class TaskMapper {

    public Task toEntity(TaskRequest request, Category category, User user) {
        return new Task(
                request.getTitle(),
                request.getDescription(),
                request.getStatus(),
                category,
                user);
    }

    public TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getCategory().getId(),
                task.getUser().getId()
        );
    }

    public void updateEntity(Task task, TaskRequest request, Category category, User user){
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setCategory(category);
        task.setUser(user);

    }

}
