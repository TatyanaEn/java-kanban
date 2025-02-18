package ru.practicum.java_kanban.service;

import ru.practicum.java_kanban.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager <T extends Task> implements HistoryManager{
    List<T> historyList;

    public InMemoryHistoryManager() {
        historyList = new ArrayList<>();
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            if (historyList.size() >= 10) {
                historyList.remove(0);
            }
            historyList.add((T) task);
        }
    }


    @Override
    public List<T> getHistory() {
        return historyList;
    }

}
