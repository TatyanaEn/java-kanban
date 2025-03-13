package ru.practicum.java_kanban.service;

import ru.practicum.java_kanban.model.Task;

import java.util.List;

public interface  HistoryManager<T extends Task> {

    void add(T task);

    void remove(int id);

    List<T> getHistory();
}
