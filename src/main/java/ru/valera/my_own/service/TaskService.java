
package ru.valera.my_own.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.valera.my_own.dto.TaskFilter;
import ru.valera.my_own.dto.TaskRequest;
import ru.valera.my_own.dto.TaskResponse;
import ru.valera.my_own.entity.OutboxEvent;
import ru.valera.my_own.enums.TaskStatus;
import ru.valera.my_own.event.TaskCreatedEvent;
import ru.valera.my_own.exception.TaskNotFoundException;
import ru.valera.my_own.mapper.TaskMapper;
import ru.valera.my_own.repository.OutboxEventRepository;
import ru.valera.my_own.repository.TaskRepository;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final TaskMapper taskMapper;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public TaskResponse create(TaskRequest request) {
        log.info("Creating task for userId={}, categoryId={}",
                request.getUserId(), request.getCategoryId());

        var user = userService.getEntityById(request.getUserId());
        var category = categoryService.getEntityById(request.getCategoryId());

        var task = taskMapper.toEntity(request, category, user);
        var savedTask = taskRepository.save(task);

        var event = new TaskCreatedEvent(
                savedTask.getId(),
                savedTask.getTitle(),
                savedTask.getUser().getId()
        );

        var outboxEvent = new OutboxEvent();
        outboxEvent.setEventType("TASK_CREATED");
        outboxEvent.setPayload(objectMapper.writeValueAsString(event));

        outboxEventRepository.save(outboxEvent);

        log.info("Task created successfully: taskId={}", savedTask.getId());

        return taskMapper.toResponse(savedTask);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "tasks", key = "#taskId")
    public TaskResponse getTaskById(Long taskId) {
        log.info("Loading task from database: taskId={}", taskId);

        var task = taskRepository.findById(taskId)
                .orElseThrow(() -> {
                    log.warn("Task not found: taskId={}", taskId);
                    return new TaskNotFoundException(
                            "task " + taskId + " is not found"
                    );
                });

        return taskMapper.toResponse(task);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByUserId(Long userId) {
        log.debug("Getting tasks for userId={}", userId);

        var user = userService.getEntityById(userId);

        return user.getTasks().stream()
                .map(taskMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByStatus(TaskStatus status) {
        log.debug("Getting tasks with status={}", status);

        return taskRepository.findByStatus(status).stream()
                .map(taskMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<TaskResponse> getTasksByFilter(TaskFilter filter, Pageable pageable) {
        var page = taskRepository.findByFilter(
                filter.status(), filter.from(), filter.to(), pageable
        );
        return page.map(taskMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getAllTasks() {
        log.debug("Getting all tasks");

        return taskRepository.findAll().stream()
                .map(taskMapper::toResponse)
                .toList();
    }

    @Transactional
    @CacheEvict(value = "tasks", key = "#taskId")
    public TaskResponse update(Long taskId, TaskRequest request) {
        log.info("Updating task: taskId={}", taskId);

        var task = taskRepository.findById(taskId)
                .orElseThrow(() -> {
                    log.warn("Task not found for update: taskId={}", taskId);
                    return new TaskNotFoundException(
                            "task " + taskId + " is not found"
                    );
                });

        var user = userService.getEntityById(request.getUserId());
        var category = categoryService.getEntityById(request.getCategoryId());

        taskMapper.updateEntity(task, request, category, user);

        var updatedTask = taskRepository.save(task);

        log.info("Task updated successfully: taskId={}", updatedTask.getId());

        return taskMapper.toResponse(updatedTask);
    }

    @Transactional
    @CacheEvict(value = "tasks", key = "#taskId")
    public void delete(Long taskId) {
        log.info("Deleting task: taskId={}", taskId);

        var task = taskRepository.findById(taskId)
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
