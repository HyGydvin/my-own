
package ru.valera.my_own.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.valera.my_own.entity.Category;
import ru.valera.my_own.entity.Task;
import ru.valera.my_own.entity.User;
import ru.valera.my_own.enums.TaskStatus;
import ru.valera.my_own.repository.CategoryRepository;
import ru.valera.my_own.repository.TaskRepository;
import ru.valera.my_own.repository.UserRepository;
import ru.valera.my_own.service.TaskEventProducer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TaskIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TaskRepository taskRepository;

    @MockitoBean
    private TaskEventProducer taskEventProducer;

    @Test
    void createTask_shouldSaveTaskAndReturnResponse() throws Exception {
        // Подготавливаем данные в БД
        User user = userRepository.save(
                new User("Integration User", "integration@example.com")
        );

        Category category = categoryRepository.save(
                new Category("Integration")
        );

        String requestBody = """
                {
                    "title": "Integration task",
                    "description": "Testing task creation",
                    "status": "TODO",
                    "categoryId": %d,
                    "userId": %d
                }
                """.formatted(category.getId(), user.getId());

        // Отправляем HTTP POST
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Integration task"))
                .andExpect(jsonPath("$.description").value("Testing task creation"))
                .andExpect(jsonPath("$.status").value("TODO"))
                .andExpect(jsonPath("$.categoryId").value(category.getId()))
                .andExpect(jsonPath("$.userId").value(user.getId()));

        // Проверяем, что задача действительно появилась в БД
        assertThat(taskRepository.findAll())
                .anySatisfy(task ->
                        assertThat(task.getTitle()).isEqualTo("Integration task"));
    }

    @Test
    void getTaskById_shouldReturnTaskFromDatabase() throws Exception {
        // Подготавливаем данные в БД
        User user = userRepository.save(
                new User("Integration User", "integration2@example.com")
        );

        Category category = categoryRepository.save(
                new Category("Integration")
        );

        Task task = taskRepository.saveAndFlush(
                new Task(
                        "Saved task",
                        "Task description",
                        TaskStatus.IN_PROGRESS,
                        category,
                        user
                )
        );

        // Отправляем HTTP GET
        mockMvc.perform(get("/api/tasks/{id}", task.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(task.getId()))
                .andExpect(jsonPath("$.title").value("Saved task"))
                .andExpect(jsonPath("$.description").value("Task description"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.categoryId").value(category.getId()))
                .andExpect(jsonPath("$.userId").value(user.getId()));
    }
}
