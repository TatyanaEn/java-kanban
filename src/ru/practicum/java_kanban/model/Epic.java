package ru.practicum.java_kanban.model;

import java.util.ArrayList;
import java.util.HashMap;

public class Epic extends Task {

    private ArrayList<Subtask> subtasks;


    public Epic(String name, String description) {
        super(name, description, StatusTask.NEW);
        subtasks = new ArrayList<>();
    }

    public Epic(Epic original) {
        super(original.getName(), original.getDescription(), StatusTask.NEW);
        setId(original.getId());
        subtasks = original.getSubtasks();
    }


    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks);
    }

    public void addSubtask(Subtask subtask) {
        if (subtask != null) {
            if (subtask.getEpic() != null) // если у подзадачи был уже указан эпик , то нужно убрать старую связь
                if (!subtask.getEpic().equals(this)) {
                    subtask.getEpic().deleteSubtask(subtask);
                }
            if (!subtasks.contains(subtask)) {
                subtasks.add(subtask);
            } else {
                int indexToReplace = subtasks.indexOf(subtask);
                if (indexToReplace != -1) {
                    subtasks.set(indexToReplace, subtask);
                }
            }
            subtask.setEpic(this);
            calculateStatus();
        }
    }

    public void deleteSubtask(Subtask subtask) {
        if (subtasks.contains(subtask)) {
            subtasks.remove(subtask);
            calculateStatus();
        }
    }

    public void calculateStatus() {
        int countDone = 0;
        if (subtasks == null) {
            this.status = StatusTask.NEW;
            return;
        }
        if (subtasks.size() == 0) {
            this.status = StatusTask.NEW;
            return;
        }
        for (Subtask item : subtasks) {
            if (item.getStatus().equals(StatusTask.DONE)) {
                countDone++;
            }
        }
        if (countDone == subtasks.size()) {
            this.status = StatusTask.DONE;
            return;
        }
        int countNew = 0;
        for (Subtask item : subtasks) {
            if (item.getStatus().equals(StatusTask.NEW)) {
                countNew++;
            }
        }
        if (countNew == subtasks.size()) {
            this.status = StatusTask.NEW;
            return;
        }
        this.status = StatusTask.IN_PROGRESS;
    }

    @Override
    public String toString() {
        String result = "Epic{" +
                "id = " + this.getId() +
                ", name = '" + this.getName() +
                "', description = '" + this.getDescription() +
                "', status = " + this.getStatus() +
                ", subtasks=[";
        String resultSubtask = "";
        for (Subtask subtask : subtasks) {
            if (!resultSubtask.equals(""))
                resultSubtask = resultSubtask + ", ";
            resultSubtask = resultSubtask + subtask.toString();
        }
        result = result + resultSubtask + "]}";
        return result;
    }


}
