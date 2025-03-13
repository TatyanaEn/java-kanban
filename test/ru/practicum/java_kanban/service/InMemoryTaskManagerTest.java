package ru.practicum.java_kanban.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.java_kanban.model.Epic;
import ru.practicum.java_kanban.model.StatusTask;
import ru.practicum.java_kanban.model.Subtask;
import ru.practicum.java_kanban.model.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private static InMemoryTaskManager taskManager;

    @BeforeEach
    void BeforeEach() {
        taskManager = (InMemoryTaskManager) Managers.getDefault();
    }

    @Test
    void addNewTask() {

        Task task = new Task("Помыть машину", "Записаться на мойку или помыть машину самому",
                StatusTask.NEW);
        final int taskId = taskManager.createTask(task);

        final Task savedTask = taskManager.getTaskById(taskId);
        assertNotNull(savedTask, "Задача не найдена.");

        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void addNewEpic() {

        Epic epic1 = new Epic("Важный эпик 1", "очень очень очень важный список задач");
        final int epicId1 = taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача1", "Описание подзадачи1",
                StatusTask.NEW, taskManager.getEpicById(epicId1));
        Subtask subtask2 = new Subtask("Подзадача2", "Описание подзадачи2",
                StatusTask.NEW, taskManager.getEpicById(epicId1));
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        final Task savedEpic = taskManager.getEpicById(epicId1);
        assertNotNull(savedEpic, "Задача не найдена.");

        assertEquals(epic1, savedEpic, "Задачи не совпадают.");

        final List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic1, epics.get(0), "Задачи не совпадают.");
    }

    @Test
    void addNewSubTask() {

        Epic epic1 = new Epic("Важный эпик 1", "очень очень очень важный список задач");
        int epicId1 = taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача1", "Описание подзадачи1",
                StatusTask.NEW, taskManager.getEpicById(epicId1));
        Subtask subtask2 = new Subtask("Подзадача2", "Описание подзадачи2",
                StatusTask.NEW, taskManager.getEpicById(epicId1));

        int subTaskId1 = taskManager.createSubtask(subtask1);
        int subTaskId2 = taskManager.createSubtask(subtask2);

        final Subtask savedSubtask = taskManager.getSubtaskById(subTaskId1);
        assertNotNull(savedSubtask, "Задача не найдена.");

        assertEquals(subtask1, savedSubtask, "Задачи не совпадают.");

        final List<Subtask> subtasks = taskManager.getSubtasks();

        assertNotNull(subtasks, "Задачи не возвращаются.");
        assertEquals(2, subtasks.size(), "Неверное количество задач.");
        assertEquals(subtask1, subtasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void deleteSubTask() {
        Epic epic1 = new Epic("Важный эпик 1", "очень очень очень важный список задач");
        int epicId1 = taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача1", "Описание подзадачи1",
                StatusTask.NEW, taskManager.getEpicById(epicId1));
        Subtask subtask2 = new Subtask("Подзадача2", "Описание подзадачи2",
                StatusTask.NEW, taskManager.getEpicById(epicId1));

        int subTaskId1 = taskManager.createSubtask(subtask1);
        int subTaskId2 = taskManager.createSubtask(subtask2);
        taskManager.deleteSubtaskById(subTaskId1);


        final int subtaskId = subtask1.getId();
        final Subtask savedSubtask = taskManager.getSubtaskById(subtaskId);
        assertNull(savedSubtask, "Задача найдена.");

        assertEquals(false, taskManager.getEpicById(epicId1).getSubtasks().contains(subtask1)
                , "Внутри эрика есть неактуальная подзадача");
    }

}