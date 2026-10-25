package ru.valera.my_own.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.valera.my_own.dto.TaskRequest;
import ru.valera.my_own.dto.TaskResponse;
import ru.valera.my_own.enums.TaskStatus;
import ru.valera.my_own.service.TaskService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public TaskResponse create(@Valid @RequestBody TaskRequest request){
        return taskService.create(request);
    }

    @GetMapping("/{id}")
    public TaskResponse getTaskById(@PathVariable("id") Long taskId){
        return taskService.getTaskById(taskId);
    }

    @GetMapping("/user/{userId}")
    public List<TaskResponse> getUserTasks (@PathVariable Long userId){
        return taskService.getTasksByUserId(userId);
    }

    @GetMapping("/status/{status}")
    public List<TaskResponse> getTasksByStatus(@PathVariable TaskStatus status){
        return taskService.getTasksByStatus(status);
    }

    @GetMapping
    public List<TaskResponse> getAllTasks(){
        return taskService.getAllTasks();
    }

    @PutMapping("/{id}")
    public TaskResponse update (@PathVariable Long id,@Valid @RequestBody TaskRequest request){
        return taskService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete (@PathVariable Long id){
        taskService.delete(id);
    }

}
