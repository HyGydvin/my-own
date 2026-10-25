package ru.valera.my_own.exception;

public class TaskNotFoundException extends RuntimeException{

    public TaskNotFoundException (String message) {
        super (message);
    }

}
