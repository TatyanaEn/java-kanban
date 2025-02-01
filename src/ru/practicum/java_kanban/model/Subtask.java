package ru.practicum.java_kanban.model;

import java.util.ArrayList;

public class Subtask extends Task{
    private Epic epic; // связь с эпиком-родителем

    // 2 конструктора
    //1ый без указания эпика-родителя
    public Subtask(String name, String description, StatusTask statusTask) {
        super(name, description, statusTask);
    }


    //2ой с передачей в параметры эпика- родителя
    public Subtask(String name, String description,   StatusTask statusTask, Epic epic) {
        super(name, description, statusTask);
        if (epic != null) {
            this.epic = epic;
            epic.addSubtask(this);
        }
    }

    public Epic getEpic() {
        return epic;
    }

    // метод для установки связи подзадачи с эпиком

    public void setEpic(Epic epic) {
        if (epic != null)
            if (!epic.equals(this.epic))  {
                if (this.epic != null) {     // если у подзадачи был уже указан эпик , то нужно убрать старую связь
                    if (!this.epic.equals(epic)) {
                        this.epic.deleteSubtask(this);
                    }
                    this.epic = epic;
                    epic.addSubtask(this);
                }
        }
    }
    @Override
    public void setId(int id) {
        epic.deleteSubtask(this);
        super.setId(id);
        epic.addSubtask(this);
    }

    @Override
    public String toString() {
        String result = "Subtask{" +
                "id = " + this.getId() +
                ", name = '" + this.getName() +
                "', description = '" + this.getDescription() +
                "', status = " + this.getStatus();
        if (epic != null)
            result = result + ", epicId=" + epic.getId() +'}';
        return result;
    }
}
