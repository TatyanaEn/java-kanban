package ru.practicum.java_kanban.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subtaskIds;

    private LocalDateTime endTime;

    public Epic(String name, String description, Integer id) {
        super(name, description, StatusTask.NEW, Duration.ofMinutes(0), null, id);
        subtaskIds = new ArrayList<>();
    }

    public Epic(String name, String description) {
        super(name, description, StatusTask.NEW, Duration.ofMinutes(0), null);
        subtaskIds = new ArrayList<>();
    }

    public Epic(Epic original) {
        super(original);
        setId(original.getId());
        subtaskIds = original.getSubtasks();
    }

    public ArrayList<Integer> getSubtasks() {
        return new ArrayList<>(subtaskIds);
    }

    public void addSubtask(Subtask subtask) {
        if (subtask != null) {
            if (!subtaskIds.contains(subtask.getId())) {
                subtaskIds.add(subtask.getId());
            }
            subtask.setEpicId(this.getId());
        }
    }

    public void deleteSubtask(Subtask subtask) {
        if (subtaskIds.contains(subtask.getId())) {
            subtaskIds.remove(subtask.getId());
            subtask.setEpicId(null);
        }
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public TypeTask getType() {
        return TypeTask.EPIC;
    }
}
