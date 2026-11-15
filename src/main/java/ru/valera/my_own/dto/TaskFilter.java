package ru.valera.my_own.dto;

import org.springframework.format.annotation.DateTimeFormat;
import ru.valera.my_own.enums.TaskStatus;

import java.time.LocalDateTime;

public record TaskFilter(
        TaskStatus status,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
) {
}
