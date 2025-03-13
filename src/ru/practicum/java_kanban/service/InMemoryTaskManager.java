package ru.practicum.java_kanban.service;

import ru.practicum.java_kanban.model.Epic;
import ru.practicum.java_kanban.model.Subtask;
import ru.practicum.java_kanban.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private int count = 0;

    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Subtask> subtasks;
    private HashMap<Integer, Epic> epics;

    HistoryManager historyManager;

    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.epics = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
    }

    @Override
    public int getNewId() {
        count++;
        return count;
    }

    @Override
    public ArrayList<Task> getTasks() {
        ArrayList<Task> result = new ArrayList<>();
        for (Task task : tasks.values()) {
            result.add(new Task(task));
        }
        return result;
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        ArrayList<Subtask> result = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            result.add(new Subtask(subtask));
        }
        return result;
    }

    @Override
    public ArrayList<Epic> getEpics() {
        ArrayList<Epic> result = new ArrayList<>();
        for (Epic epic : epics.values()) {
            result.add(new Epic(epic));
        }
        return result;

    }

    @Override
    public void deleteTasks() {
        for (Task item : getHistory()) {
            historyManager.remove(item.getId());
        }
        tasks.clear();

    }

    @Override
    public void deleteSubtasks() {
            for (Subtask subtask : subtasks.values()) {
                subtask.getEpic().deleteSubtask(subtask);
                historyManager.remove(subtask.getId());
            }
            subtasks.clear();
    }

    @Override
    public void deleteEpics() {
            for (Epic epic : epics.values()){
                for (Subtask subtask : epic.getSubtasks()) {
                    subtasks.remove(subtask.getId());
                    historyManager.remove(subtask.getId());
                }
                historyManager.remove(epic.getId());
            }
            epics.clear();
    }

    @Override
    public Task getTaskById(int id) {
        if (tasks.get(id) != null) {
            historyManager.add(tasks.get(id));
            return new Task(tasks.get(id));
        } else
            return null;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        if (subtasks.get(id) != null) {
            historyManager.add(subtasks.get(id));
            return new Subtask(subtasks.get(id));
        } else
            return null;
    }

    @Override
    public Epic getEpicById(int id) {
        if (epics.get(id) != null) {
            historyManager.add(epics.get(id));
            return new Epic(epics.get(id));
        } else
            return null;
    }


    @Override
    public int createTask(Task task) {
        int newId = -1;
        if (task != null) {
            Task newTask = new Task(task);
            newId = getNewId();
            newTask.setId(newId);
            tasks.put(newTask.getId(), newTask);
        }
        return newId;
    }

    @Override
    public int createSubtask(Subtask subtask) {
        int newId = -1;
        if (subtask != null) {
            Subtask newSubtask = new Subtask(subtask);
            newId = getNewId();
            newSubtask.setId(newId);
            subtasks.put(newSubtask.getId(), newSubtask);
        }
        return newId;
    }

    @Override
    public int createEpic(Epic epic) {
        int newId = -1;
        if (epic != null) {
            Epic newEpic = new Epic(epic);
            newId = getNewId();
            newEpic.setId(newId);
            epics.put(newEpic.getId(), newEpic);
        }
        return newId;
    }

    @Override
    public void updateTask(Task task) {
        if (task != null) {
            Task newTask = new Task(task);
            newTask.setId(task.getId());
            tasks.put(newTask.getId(), newTask);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask != null) {
            Subtask newSubtask = new Subtask(subtask);
            newSubtask.setId(subtask.getId());
            subtasks.put(newSubtask.getId(), newSubtask);
            Epic parentEpic = newSubtask.getEpic();
            if (parentEpic != null) {
                parentEpic.addSubtask(newSubtask);
            }
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic != null) {
            Epic newEpic = new Epic(epic);
            newEpic.setId(epic.getId());
            epics.put(newEpic.getId(), newEpic);
        }
    }

    @Override
    public void deleteTaskById(int id) {
        if (tasks == null) return;
        historyManager.remove(id);
        tasks.remove(id);
    }

    @Override
    public void deleteSubtaskById(int id) {
        if (subtasks == null) return;
        Subtask subtask = subtasks.get(id);
        if (subtask == null) return;
        Epic epic = epics.get(subtask.getEpic().getId());
        epic.deleteSubtask(subtask);
        subtasks.remove(id);
        historyManager.remove(id);
    }



    @Override
    public void deleteEpicById(int id) {
        if (epics == null)
            return;
        if (epics.containsKey(id)) {
            ArrayList<Subtask> subtaskList = epics.get(id).getSubtasks();

            for (Subtask item : subtaskList) {
                subtasks.remove(item.getId());
            }
            epics.remove(id);
            historyManager.remove(id);
        }

    }

    @Override
    public ArrayList<Subtask> getSubtasksFromEpic(Epic epic) {
        if (epic != null) {
            return epic.getSubtasks();
        }
        else
            return null;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

}
