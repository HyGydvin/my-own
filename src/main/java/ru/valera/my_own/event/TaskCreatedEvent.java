package ru.valera.my_own.event;

public record TaskCreatedEvent (
        Long taskId,
        String title,
        Long userId
){
}