package ru.practicum.java_kanban.service;

import ru.practicum.java_kanban.model.Epic;
import ru.practicum.java_kanban.model.Subtask;
import ru.practicum.java_kanban.model.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    ArrayList<Task> getTasks();

    ArrayList<Subtask> getSubtasks();

    ArrayList<Epic> getEpics();

    void deleteTasks();

    void deleteSubtasks();

    void deleteEpics();

    Task getTaskById(Integer id);

    Subtask getSubtaskById(Integer id);

    Epic getEpicById(Integer id);

    Integer createTask(Task task);

    Integer createSubtask(Subtask subtask);

    Integer createEpic(Epic epic);

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    void deleteTaskById(Integer id);

    void deleteSubtaskById(Integer id);

    void deleteEpicById(Integer id);

    ArrayList<Subtask> getSubtasksFromEpic(Epic epic);

    Integer getNewId();

    List<Task> getHistory();
}
