package ru.practicum.java_kanban.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {

    private String name;
    private String description;
    private Integer id;
    private Duration duration;
    private LocalDateTime startTime;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    protected StatusTask status;


    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Integer getId() {
        return id;
    }

    public StatusTask getStatus() {
        return status;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStatus(StatusTask status) {
        this.status = status;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public TypeTask getType() {
        return TypeTask.TASK;
    }

    public Task(String name, String description, StatusTask status, Duration duration, LocalDateTime startTime, Integer id) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
        this.id = id;
    }

    public Task(String name, String description, StatusTask status, Duration duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
        this.id = null;
    }

    public Task(Task original) {
        this.name = original.name;
        this.description = original.description;
        this.status = original.status;
        this.duration = original.duration;
        this.startTime = original.startTime;
        this.id = original.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getEndTime() {
        if (startTime != null)
            return startTime.plus(duration);
        else
            return null;
    }

    @Override
    public String toString() {
        return getId() + "," + getType() + "," + getName() + "," + getStatus() + "," + getDescription()
                + "," + Long.toString(getDuration().toMinutes()) + "," + (getStartTime() == null ? "null" : getStartTime().format(DATE_TIME_FORMATTER));
    }

    @Override
    public boolean equals(Object o) { // добавили и переопределили equals
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        if (Objects.equals(id, task.id))
            return true;
        return Objects.equals(name, task.name) &&
                Objects.equals(description, task.description) &&
                Objects.equals(status, task.status) &&
                duration.equals(task.duration) &&
                Objects.equals(startTime, task.startTime);
    }

    @Override
    public int hashCode() {
        int hash = id;

        if (name != null) {
            hash = hash + name.hashCode();
        }
        hash = hash * 31;

        if (description != null) {
            hash = hash + description.hashCode();
        }
        if (status != null) {
            hash = hash + status.hashCode();
        }
        if (duration != null) {
            hash = hash + duration.hashCode();
        }
        if (startTime != null) {
            hash = hash + startTime.hashCode();
        }
        return hash;
    }
}
