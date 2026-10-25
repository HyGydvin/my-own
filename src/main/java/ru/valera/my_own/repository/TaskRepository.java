package ru.valera.my_own.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.valera.my_own.entity.Task;
import ru.valera.my_own.enums.TaskStatus;

import java.util.List;


@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByStatus (TaskStatus status);
}
