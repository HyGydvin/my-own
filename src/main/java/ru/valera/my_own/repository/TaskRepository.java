package ru.valera.my_own.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.valera.my_own.entity.Task;
import ru.valera.my_own.enums.TaskStatus;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByStatus(TaskStatus status);

    @Query("""
            select t from Task t
            where t.status = coalesce(:status, t.status)
              and t.createdAt >= coalesce(:from, t.createdAt)
              and t.createdAt <= coalesce(:to, t.createdAt)
            """)
    Page<Task> findByFilter(
            @Param("status") TaskStatus status,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable);
}
