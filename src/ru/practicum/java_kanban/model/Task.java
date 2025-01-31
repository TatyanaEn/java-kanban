package ru.practicum.java_kanban.model;

import java.util.Objects;

public class Task {

    private String name;
    private String description;
    private int id;
    protected StatusTask status;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public StatusTask getStatus() {
        return status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public Task(String name, String description, int id, StatusTask status) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id = " + id +
                ", name = '" + name +
                "', description = '" + description +
                "', status = " + status +
                '}';
    }

    @Override
    public boolean equals(Object o) { // добавили и переопределили equals
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        if ( Objects.equals(id, task.id))
            return true;
        return Objects.equals(name, task.name) &&
                Objects.equals(description, task.description) &&
                Objects.equals(status, task.status);
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
        return hash;
    }
}
