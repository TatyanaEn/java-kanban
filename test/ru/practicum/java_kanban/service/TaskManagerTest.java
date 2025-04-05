package ru.practicum.java_kanban.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.java_kanban.model.Epic;
import ru.practicum.java_kanban.model.StatusTask;
import ru.practicum.java_kanban.model.Subtask;
import ru.practicum.java_kanban.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    @Test
    void addNewTask() {

        Task task = new Task("Помыть машину", "Записаться на мойку или помыть машину самому",
                StatusTask.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("04.04.2025 11:00" , DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
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
                StatusTask.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("04.04.2025 11:00" , DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                taskManager.getEpicById(epicId1));
        Subtask subtask2 = new Subtask("Подзадача2", "Описание подзадачи2",
                StatusTask.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("04.04.2025 11:00" , DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                taskManager.getEpicById(epicId1));
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
                StatusTask.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("04.04.2025 11:00" , DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                taskManager.getEpicById(epicId1));
        Subtask subtask2 = new Subtask("Подзадача2", "Описание подзадачи2",
                StatusTask.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("04.04.2025 11:00" , DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                taskManager.getEpicById(epicId1));

        int subTaskId1 = taskManager.createSubtask(subtask1);
        int subTaskId2 = taskManager.createSubtask(subtask2);

        final Subtask savedSubtask = taskManager.getSubtaskById(subTaskId1);
        assertNotNull(savedSubtask, "Задача не найдена.");

        assertEquals(subtask1, savedSubtask, "Задачи не совпадают.");

        final List<Subtask> subtasks = taskManager.getSubtasks();

        assertNotNull(subtasks, "Задачи не возвращаются.");
        assertEquals(2, subtasks.size(), "Неверное количество задач.");
    }

    @Test
    void deleteTask() {
        Task task = new Task("Помыть машину", "Записаться на мойку или помыть машину самому",
                StatusTask.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("04.04.2025 11:00" , DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
        final int taskId = taskManager.createTask(task);
        taskManager.deleteTaskById(taskId);

        final Task savedTask = taskManager.getTaskById(taskId);
        assertNull(savedTask, "Задача не удалена.");

    }

    @Test
    void deleteSubTask() {
        Epic epic1 = new Epic("Важный эпик 1", "очень очень очень важный список задач");
        int epicId1 = taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача1", "Описание подзадачи1",
                StatusTask.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("04.04.2025 11:00" , DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                taskManager.getEpicById(epicId1));
        Subtask subtask2 = new Subtask("Подзадача2", "Описание подзадачи2",
                StatusTask.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("04.04.2025 11:00" , DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                taskManager.getEpicById(epicId1));

        int subTaskId1 = taskManager.createSubtask(subtask1);
        int subTaskId2 = taskManager.createSubtask(subtask2);
        taskManager.deleteSubtaskById(subTaskId1);


        final int subtaskId = subtask1.getId();
        final Subtask savedSubtask = taskManager.getSubtaskById(subtaskId);
        assertNull(savedSubtask, "Подзадача не удалена");

        assertEquals(false, taskManager.getEpicById(epicId1).getSubtasks().contains(subtask1)
                , "Внутри эрика есть неактуальная подзадача");
    }

    @Test
    void deleteEpic() {
        Epic epic1 = new Epic("Важный эпик 1", "очень очень очень важный список задач");
        int epicId1 = taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача1", "Описание подзадачи1",
                StatusTask.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("04.04.2025 11:00" , DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                taskManager.getEpicById(epicId1));
        Subtask subtask2 = new Subtask("Подзадача2", "Описание подзадачи2",
                StatusTask.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("04.04.2025 11:00" , DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                taskManager.getEpicById(epicId1));

        int subTaskId1 = taskManager.createSubtask(subtask1);
        int subTaskId2 = taskManager.createSubtask(subtask2);
        taskManager.deleteEpicById(epicId1);

        final Epic savedEpic = taskManager.getEpicById(epicId1);
        assertNull(savedEpic, "Эпик не удален.");

    }

    @Test
    void checkStatusNEW() {
        Epic epic1 = new Epic("Важный эпик 1", "очень очень очень важный список задач");
        int epicId1 = taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача1", "Описание подзадачи1",
                StatusTask.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("04.04.2025 11:00" , DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                taskManager.getEpicById(epicId1));
        Subtask subtask2 = new Subtask("Подзадача2", "Описание подзадачи2",
                StatusTask.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("04.04.2025 11:00" , DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                taskManager.getEpicById(epicId1));

        int subTaskId1 = taskManager.createSubtask(subtask1);
        int subTaskId2 = taskManager.createSubtask(subtask2);

        final Epic savedEpic = taskManager.getEpicById(epicId1);

        assertEquals(savedEpic.getStatus(), StatusTask.NEW, "Статус эпика не равен NEW");

    }

    @Test
    void checkStatusDONE() {
        Epic epic1 = new Epic("Важный эпик 1", "очень очень очень важный список задач");
        int epicId1 = taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача1", "Описание подзадачи1",
                StatusTask.DONE, Duration.ofMinutes(30),
                LocalDateTime.parse("04.04.2025 11:00" , DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                taskManager.getEpicById(epicId1));
        Subtask subtask2 = new Subtask("Подзадача2", "Описание подзадачи2",
                StatusTask.DONE, Duration.ofMinutes(30),
                LocalDateTime.parse("04.04.2025 11:00" , DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                taskManager.getEpicById(epicId1));

        int subTaskId1 = taskManager.createSubtask(subtask1);
        int subTaskId2 = taskManager.createSubtask(subtask2);

        final Epic savedEpic = taskManager.getEpicById(epicId1);

        assertEquals(savedEpic.getStatus(), StatusTask.DONE, "Статус эпика не равен DONE");

    }

    @Test
    void checkStatusNEWandDONE() {
        Epic epic1 = new Epic("Важный эпик 1", "очень очень очень важный список задач");
        int epicId1 = taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача1", "Описание подзадачи1",
                StatusTask.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("04.04.2025 11:00" , DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                taskManager.getEpicById(epicId1));
        Subtask subtask2 = new Subtask("Подзадача2", "Описание подзадачи2",
                StatusTask.DONE, Duration.ofMinutes(30),
                LocalDateTime.parse("04.04.2025 11:00" , DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                taskManager.getEpicById(epicId1));

        int subTaskId1 = taskManager.createSubtask(subtask1);
        int subTaskId2 = taskManager.createSubtask(subtask2);

        final Epic savedEpic = taskManager.getEpicById(epicId1);

        assertEquals(savedEpic.getStatus(), StatusTask.IN_PROGRESS, "Статус эпика не равен IN_PROGRESS");

    }

    @Test
    void checkStatusIN_PROGRESS() {
        Epic epic1 = new Epic("Важный эпик 1", "очень очень очень важный список задач");
        int epicId1 = taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача1", "Описание подзадачи1",
                StatusTask.IN_PROGRESS, Duration.ofMinutes(30),
                LocalDateTime.parse("04.04.2025 11:00" , DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                taskManager.getEpicById(epicId1));
        Subtask subtask2 = new Subtask("Подзадача2", "Описание подзадачи2",
                StatusTask.IN_PROGRESS, Duration.ofMinutes(30),
                LocalDateTime.parse("04.04.2025 11:00" , DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                taskManager.getEpicById(epicId1));

        int subTaskId1 = taskManager.createSubtask(subtask1);
        int subTaskId2 = taskManager.createSubtask(subtask2);

        final Epic savedEpic = taskManager.getEpicById(epicId1);

        assertEquals(savedEpic.getStatus(), StatusTask.IN_PROGRESS, "Статус эпика не равен IN_PROGRESS");

    }

    @Test
    void validateBindingToTask() {
        Epic epic1 = new Epic("Важный эпик 1", "очень очень очень важный список задач");
        int epicId1 = taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача1", "Описание подзадачи1",
                StatusTask.IN_PROGRESS, Duration.ofMinutes(30),
                LocalDateTime.parse("04.04.2025 11:00" , DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                taskManager.getEpicById(epicId1));
        int subTaskId1 = taskManager.createSubtask(subtask1);

        final Epic savedEpic = taskManager.getEpicById(epicId1);
        final Subtask savedSubTask = taskManager.getSubtaskById(subTaskId1);

        assertEquals(savedSubTask.getEpicId() , savedEpic.getId(), "Отсуствует связанный эпик");
    }

    @Test
    void validateCrossingTasks() {
        Epic epic1 = new Epic("Важный эпик 1", "очень очень очень важный список задач", 3);
        int id3 = taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача1", "Описание подзадачи1",
                StatusTask.NEW, Duration.ofMinutes(35), LocalDateTime.parse("04.04.2025 14:00" , DATE_TIME_FORMATTER), taskManager.getEpicById(id3), 4);
        Subtask subtask2 = new Subtask("Подзадача2", "Описание подзадачи2",
                StatusTask.NEW, Duration.ofMinutes(20), LocalDateTime.parse("04.04.2025 17:00" , DATE_TIME_FORMATTER), taskManager.getEpicById(id3), 5);

        Subtask subtask3 = new Subtask("Подзадача3", "Описание подзадачи3",
                StatusTask.NEW, Duration.ofMinutes(40), LocalDateTime.parse("04.04.2025 14:10" , DATE_TIME_FORMATTER), taskManager.getEpicById(id3), 6);
        int id4 = taskManager.createSubtask(subtask1);
        int id5 = taskManager.createSubtask(subtask2);
        int id6 = taskManager.createSubtask(subtask3);

        final Subtask savedSubTask = taskManager.getSubtaskById(id6);

        assertNull(savedSubTask, "Подзадача добавлена, несмотря на наличие перечечения.");
    }


}
