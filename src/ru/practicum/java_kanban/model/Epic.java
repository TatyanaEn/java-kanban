package ru.practicum.java_kanban.model;

import java.util.ArrayList;
import java.util.HashMap;

public class Epic extends Task{

    private HashMap<Integer, Subtask> subtasks;

    public Epic(String name, String description, int id) {
        super(name, description, id, StatusTask.NEW);
        subtasks = new HashMap<>();
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(Subtask subtask){
        if (subtask != null) {
            if (subtask.getEpic() != null) // если у подзадачи был уже указан эпик , то нужно убрать старую связь
                if (!subtask.getEpic().equals(this)) {
                    subtask.getEpic().deleteSubtask(subtask);
                }
            subtasks.put(subtask.getId(), subtask);
            subtask.setEpic(this);
            calculateStatus();
        }
    }

    public void deleteSubtask(Subtask subtask){
        if (subtasks.containsValue(subtask)) {
            subtasks.remove(subtask.getId());
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
        for (Subtask item : subtasks.values()) {
            if (item.getStatus().equals(StatusTask.DONE)) {
                countDone ++;
            }
        }
        if (countDone == subtasks.size()) {
            this.status = StatusTask.DONE;
            return;
        }
        int countNew = 0;
        for (Subtask item : subtasks.values()) {
            if (item.getStatus().equals(StatusTask.NEW)) {
                countNew ++;
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
        for (Subtask subtask : subtasks.values()) {
            if (resultSubtask.equals(""))
                resultSubtask = resultSubtask + ", ";
            resultSubtask = resultSubtask + subtask.toString();
        }
        result = result + resultSubtask +"]}";
        return result;
    }


}
