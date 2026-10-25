
package ru.valera.my_own.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.valera.my_own.dto.TaskRequest;
import ru.valera.my_own.dto.TaskResponse;
import ru.valera.my_own.entity.Category;
import ru.valera.my_own.entity.Task;
import ru.valera.my_own.entity.User;
import ru.valera.my_own.enums.TaskStatus;
import ru.valera.my_own.event.TaskCreatedEvent;
import ru.valera.my_own.exception.TaskNotFoundException;
import ru.valera.my_own.mapper.TaskMapper;
import ru.valera.my_own.repository.TaskRepository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final TaskMapper taskMapper;
    private final TaskEventProducer taskEventProducer;

    public TaskResponse create(TaskRequest request) {
        log.info("Creating task for userId={}, categoryId={}",
                request.getUserId(), request.getCategoryId());

        User user = userService.getEntityById(request.getUserId());
        Category category = categoryService.getEntityById(request.getCategoryId());

        Task task = taskMapper.toEntity(request, category, user);
        Task savedTask = taskRepository.save(task);

        taskEventProducer.sendTaskCreatedEvent(
                new TaskCreatedEvent(
                        savedTask.getId(),
                        savedTask.getTitle(),
                        savedTask.getUser().getId()
                )
        );

        log.info("Task created successfully: taskId={}", savedTask.getId());

        return taskMapper.toResponse(savedTask);
    }

    @Cacheable(value = "tasks", key = "#taskId")
    public TaskResponse getTaskById(Long taskId) {
        log.info("Loading task from database: taskId={}", taskId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> {
                    log.warn("Task not found: taskId={}", taskId);
                    return new TaskNotFoundException(
                            "task " + taskId + " is not found"
                    );
                });

        return taskMapper.toResponse(task);
    }

    public List<TaskResponse> getTasksByUserId(Long userId) {
        log.debug("Getting tasks for userId={}", userId);

        User user = userService.getEntityById(userId);

        return user.getTasks().stream()
                .map(taskMapper::toResponse)
                .toList();
    }

    public List<TaskResponse> getTasksByStatus(TaskStatus status) {
        log.debug("Getting tasks with status={}", status);

        return taskRepository.findByStatus(status).stream()
                .map(taskMapper::toResponse)
                .toList();
    }

    public List<TaskResponse> getAllTasks() {
        log.debug("Getting all tasks");

        return taskRepository.findAll().stream()
                .map(taskMapper::toResponse)
                .toList();
    }

    @CacheEvict(value = "tasks", key = "#taskId")
    public TaskResponse update(Long taskId, TaskRequest request) {
        log.info("Updating task: taskId={}", taskId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> {
                    log.warn("Task not found for update: taskId={}", taskId);
                    return new TaskNotFoundException(
                            "task " + taskId + " is not found"
                    );
                });

        User user = userService.getEntityById(request.getUserId());
        Category category = categoryService.getEntityById(request.getCategoryId());

        taskMapper.updateEntity(task, request, category, user);

        Task updatedTask = taskRepository.save(task);

        log.info("Task updated successfully: taskId={}", updatedTask.getId());

        return taskMapper.toResponse(updatedTask);
    }

    @CacheEvict(value = "tasks", key = "#taskId")
    public void delete(Long taskId) {
        log.info("Deleting task: taskId={}", taskId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> {
                    log.warn("Task not found for deletion: taskId={}", taskId);
                    return new TaskNotFoundException(
                            "task " + taskId + " is not found"
                    );
                });

        taskRepository.delete(task);

        log.info("Task deleted successfully: taskId={}", taskId);
    }
}
