package ru.practicum.java_kanban.service;

import ru.practicum.java_kanban.model.Epic;
import ru.practicum.java_kanban.model.Subtask;
import ru.practicum.java_kanban.model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int count = 0;

    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Subtask> subtasks;
    private HashMap<Integer, Epic> epics;

    public TaskManager() {
        this.tasks = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.epics = new HashMap<>();
    }


    public ArrayList<Task> getTasks() {
        ArrayList<Task> result = new ArrayList<>();
        for (Task task : tasks.values()) {
            result.add(task);
        }
        return result;
    }

    public ArrayList<Subtask> getSubtasks() {
        ArrayList<Subtask> result = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            result.add(subtask);
        }
        return result;
    }

    public ArrayList<Epic> getEpics() {
        ArrayList<Epic> result = new ArrayList<>();
        for (Epic epic : epics.values()) {
            result.add(epic);
        }
        return result;

    }

    public void deleteTasks() {
            tasks.clear();
    }

    public void deleteSubtasks() {
            for (Subtask subtask : subtasks.values()) {
                subtask.getEpic().deleteSubtask(subtask);
            }
            subtasks.clear();
    }

    public void deleteEpics() {
            for (Epic epic : epics.values()){
                for (Subtask subtask : epic.getSubtasks()) {
                    subtasks.remove(subtask.getId());
                }
            }
            epics.clear();
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }


    public void createTask(Task task) {
        if (task != null) {
            task.setId(getNewId());
            tasks.put(task.getId(), task);
        }
    }

    public void createSubtask(Subtask subtask) {
        if (subtask != null) {
            subtask.setId(getNewId());
            subtasks.put(subtask.getId(), subtask);
        }
    }

    public void createEpic(Epic epic) {
        if (epic != null) {
            epic.setId(getNewId());
            epics.put(epic.getId(), epic);
        }
    }

    public void updateTask(Task task) {
        if (task != null)
            tasks.put(task.getId(), task);
    }

    public void updateSubtask(Subtask subtask) {
        if (subtask != null) {
            subtasks.put(subtask.getId(), subtask);
            Epic parentEpic = subtask.getEpic();
            if (parentEpic != null) {
                parentEpic.addSubtask(subtask);
            }
        }
    }

    public void updateEpic(Epic epic) {
        if (epic != null)
            epics.put(epic.getId(), epic);
    }

    public void deleteTaskById(int id) {
        if (tasks == null) return;
        tasks.remove(id);
    }

    public void deleteSubtaskById(int id) {
        if (subtasks == null) return;
        Subtask subtask = subtasks.get(id);
        if (subtask == null) return;
        Epic epic = epics.get(subtask.getEpic().getId());
        epic.deleteSubtask(subtask);
        subtasks.remove(id);
    }



    public void deleteEpicById(int id) {
        if (epics == null)
            return;
        if (epics.containsKey(id)) {
            ArrayList<Subtask> subtaskList = epics.get(id).getSubtasks();

            for (Subtask item : subtaskList) {
                subtasks.remove(item.getId());
            }
            epics.remove(id);
        }

    }

    public ArrayList<Subtask> getSubtasksFromEpic(Epic epic) {
        if (epic != null)
            return epic.getSubtasks();
        else
            return null;
    }

    private int getNewId() {
        count++;
        return count;
    }

}
