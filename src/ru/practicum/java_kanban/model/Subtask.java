package ru.practicum.java_kanban.model;

public class Subtask extends Task {
    //private Epic epic; // связь с эпиком-родителем
    private Integer epicId;

    // 2 конструктора
    //1ый без указания эпика-родителя
    public Subtask(String name, String description, StatusTask statusTask) {
        super(name, description, statusTask);
    }


    //2ой с передачей в параметры эпика- родителя
    public Subtask(String name, String description, StatusTask statusTask, Epic epic) {
        super(name, description, statusTask);
        if (epic != null) {
            this.setEpicId(epic.getId());

        }
    }
    public Subtask(String name, String description, StatusTask statusTask, Epic epic, Integer id) {
        super(name, description, statusTask, id);
        if (epic != null) {
            this.setEpicId(epic.getId());

        }
    }

    public Subtask(String name, String description, StatusTask statusTask, Integer id) {
        super(name, description, statusTask, id);
    }

    public Subtask(Subtask original, Epic epic) {
        super(original.getName(), original.getDescription(), original.getStatus());
        setId(original.getId());
        if (epic != null) {
            this.setEpicId(epic.getId());

        }
    }

    public Subtask(Subtask original) {
        super(original.getName(), original.getDescription(), original.getStatus());
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
