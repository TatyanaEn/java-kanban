package ru.practicum.java_kanban.exception;

public class ManagerSaveException extends RuntimeException {

    public ManagerSaveException(Throwable e) {
        super(e);
    }
}