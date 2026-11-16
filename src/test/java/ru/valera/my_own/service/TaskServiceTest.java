
package ru.valera.my_own.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ru.valera.my_own.dto.TaskRequest;
import ru.valera.my_own.dto.TaskResponse;
import ru.valera.my_own.entity.Category;
import ru.valera.my_own.entity.Task;
import ru.valera.my_own.entity.User;
import ru.valera.my_own.enums.TaskStatus;
import ru.valera.my_own.event.TaskCreatedEvent;
import ru.valera.my_own.exception.CategoryNotFoundException;
import ru.valera.my_own.exception.UserNotFoundException;
import ru.valera.my_own.mapper.TaskMapper;
import ru.valera.my_own.repository.TaskRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private TaskEventProducer taskEventProducer;

    @InjectMocks
    private TaskService taskService;

    @Test
    void create_shouldSaveTaskSendEventAndReturnResponse() {
        // Arrange
        Long userId = 1L;
        Long categoryId = 2L;
        Long taskId = 3L;

        TaskRequest request = createTaskRequest(userId, categoryId);

        User user = new User("Valera", "valera@example.com");
        ReflectionTestUtils.setField(user, "id", userId);

        Category category = new Category("Work");
        ReflectionTestUtils.setField(category, "id", categoryId);

        Task task = new Task(
                "Learn testing",
                "Study JUnit and Mockito",
                TaskStatus.TODO,
                category,
                user
        );

        Task savedTask = new Task(
                "Learn testing",
                "Study JUnit and Mockito",
                TaskStatus.TODO,
                category,
                user
        );
        ReflectionTestUtils.setField(savedTask, "id", taskId);

        TaskResponse expectedResponse = new TaskResponse(
                taskId,
                "Learn testing",
                "Study JUnit and Mockito",
                TaskStatus.TODO,
                null,
                categoryId,
                userId
        );

        when(userService.getEntityById(userId)).thenReturn(user);
        when(categoryService.getEntityById(categoryId)).thenReturn(category);
        when(taskMapper.toEntity(request, category, user)).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(savedTask);
        when(taskMapper.toResponse(savedTask)).thenReturn(expectedResponse);

        // Act
        TaskResponse actualResponse = taskService.create(request);

        // Assert
        assertThat(actualResponse).isSameAs(expectedResponse);

        InOrder inOrder = inOrder(
                userService,
                categoryService,
                taskMapper,
                taskRepository,
                taskEventProducer
        );

        inOrder.verify(userService).getEntityById(userId);
        inOrder.verify(categoryService).getEntityById(categoryId);
        inOrder.verify(taskMapper).toEntity(request, category, user);
        inOrder.verify(taskRepository).save(task);
        inOrder.verify(taskEventProducer).sendTaskCreatedEvent(
                new TaskCreatedEvent(taskId, "Learn testing", userId)
        );
        inOrder.verify(taskMapper).toResponse(savedTask);

        verifyNoMoreInteractions(taskRepository, taskEventProducer);
    }

    @Test
    void create_shouldThrowExceptionWhenUserNotFound() {
        // Arrange
        Long userId = 1L;
        Long categoryId = 2L;

        TaskRequest request = createTaskRequest(userId, categoryId);

        when(userService.getEntityById(userId))
                .thenThrow(new UserNotFoundException("user " + userId + " is not found"));

        // Act & Assert
        assertThatThrownBy(() -> taskService.create(request))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("user " + userId + " is not found");

        verify(userService).getEntityById(userId);
        verifyNoInteractions(
                categoryService,
                taskMapper,
                taskRepository,
                taskEventProducer
        );
    }

    @Test
    void create_shouldThrowExceptionWhenCategoryNotFound() {
        // Arrange
        Long userId = 1L;
        Long categoryId = 2L;

        TaskRequest request = createTaskRequest(userId, categoryId);

        User user = new User("Valera", "valera@example.com");

        when(userService.getEntityById(userId)).thenReturn(user);
        when(categoryService.getEntityById(categoryId))
                .thenThrow(new CategoryNotFoundException(
                        "category " + categoryId + " is not found"
                ));

        // Act & Assert
        assertThatThrownBy(() -> taskService.create(request))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessage("category " + categoryId + " is not found");

        verify(userService).getEntityById(userId);
        verify(categoryService).getEntityById(categoryId);

        verifyNoInteractions(
                taskMapper,
                taskRepository,
                taskEventProducer
        );
    }

    @Test
    void getTaskById_shouldReturnTaskResponse() {
        // Arrange
        Long taskId = 3L;

        Task task = mock(Task.class);
        TaskResponse expectedResponse = new TaskResponse(
                taskId,
                "Learn testing",
                "Study JUnit and Mockito",
                TaskStatus.TODO,
                null,
                2L,
                1L
        );

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskMapper.toResponse(task)).thenReturn(expectedResponse);

        // Act
        TaskResponse actualResponse = taskService.getTaskById(taskId);

        // Assert
        assertThat(actualResponse).isSameAs(expectedResponse);

        verify(taskRepository).findById(taskId);
        verify(taskMapper).toResponse(task);
        verifyNoMoreInteractions(taskRepository, taskMapper);
    }

    private TaskRequest createTaskRequest(Long userId, Long categoryId) {
        TaskRequest request = new TaskRequest();

        ReflectionTestUtils.setField(request, "title", "Learn testing");
        ReflectionTestUtils.setField(request, "description", "Study JUnit and Mockito");
        ReflectionTestUtils.setField(request, "status", TaskStatus.TODO);
        ReflectionTestUtils.setField(request, "userId", userId);
        ReflectionTestUtils.setField(request, "categoryId", categoryId);

        return request;
    }
}
