package ru.practicum.java_kanban.service;

import ru.practicum.java_kanban.model.Epic;
import ru.practicum.java_kanban.model.Subtask;
import ru.practicum.java_kanban.model.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class TaskManager {
    private static int count = 0;

    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Subtask> subtasks;
    private HashMap<Integer, Epic> epics;

    public TaskManager() {
        this.tasks = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.epics = new HashMap<>();
    }


    public Collection<Task> getTasks() {
        return tasks.values();
    }

    public Collection<Subtask> getSubtasks() {
        return subtasks.values();
    }

    public Collection<Epic> getEpics() {
        return epics.values();
    }

    public void deleteTasks() {
            tasks.clear();
    }

    public void deleteSubtasks() {
            subtasks.clear();
    }

    public void deleteEpics() {
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

    public int getNewId() {
        count++;
        return count;
    }

    public void createTask(Task task) {
        if (task != null)
            tasks.put(task.getId(), task);
    }

    public void createSubtask(Subtask subtask) {
        if (subtask != null)
            subtasks.put(subtask.getId(), subtask);
    }

    public void createEpic(Epic epic) {
        if (epic != null)
            epics.put(epic.getId(), epic);
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
            HashMap<Integer, Subtask> subtaskList = epics.get(id).getSubtasks();

            for (int itemId : subtaskList.keySet()) {
                subtasks.remove(itemId);
            }
            epics.remove(id);
        }

    }

    public Collection<Subtask> getSubtasksFromEpic(Epic epic) {
        if (epic != null)
            return epic.getSubtasks().values();
        else
            return null;
    }

}
