package ru.practicum.java_kanban.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private Integer epicId;

    // 2 конструктора
    //1ый без указания эпика-родителя
    public Subtask(String name, String description, StatusTask statusTask, Duration duration, LocalDateTime startTime) {
        super(name, description, statusTask, duration, startTime);
    }


    //2ой с передачей в параметры эпика- родителя
    public Subtask(String name, String description, StatusTask statusTask, Duration duration, LocalDateTime startTime, Epic epic) {
        super(name, description, statusTask, duration, startTime);
        if (epic != null) {
            this.setEpicId(epic.getId());

        }
    }

    public Subtask(String name, String description, StatusTask statusTask, Duration duration, LocalDateTime startTime, Epic epic, Integer id) {
        super(name, description, statusTask, duration, startTime, id);
        if (epic != null) {
            this.setEpicId(epic.getId());

        }
    }

    public Subtask(String name, String description, StatusTask statusTask, Duration duration, LocalDateTime startTime, Integer id) {
        super(name, description, statusTask, duration, startTime, id);
    }

    public Subtask(Subtask original, Epic epic) {
        super(original);
        setId(original.getId());
        if (epic != null) {
            this.setEpicId(epic.getId());

        }
    }

    public Subtask(Subtask original) {
        super(original);
        if (original.epicId != null)
            this.setEpicId(original.getEpicId());
        setId(original.getId());
    }

    @Override
    public void setId(Integer id) {
        super.setId(id);
    }

    @Override
    public String toString() {
        String result = super.toString();
        if (getEpicId() != null)
            result += "," + getEpicId();
        return result;
    }

    @Override
    public TypeTask getType() {
        return TypeTask.SUBTASK;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return this.epicId;
    }

}
