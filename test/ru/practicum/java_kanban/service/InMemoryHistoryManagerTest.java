package ru.practicum.java_kanban.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import ru.practicum.java_kanban.model.StatusTask;
import ru.practicum.java_kanban.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private static InMemoryHistoryManager historyManager;

    @BeforeEach
    void beforeEach() {
        historyManager = (InMemoryHistoryManager) Managers.getDefaultHistory();
    }

    @Test
    void shouldAddTasksToHistoryList() {
        Task task1 = new Task("Помыть машину", "Записаться на мойку или помыть машину самому",
                StatusTask.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("04.04.2025 11:00" , DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")), 1);
        Task task2 = new Task( "Заехать в МФЦ", "Заехать в МФЦ за документами (окно №3)",
                StatusTask.NEW, Duration.ofMinutes(10),
                LocalDateTime.parse("04.04.2025 15:00" , DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")), 2);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task2);
        task1.setId(100);
        assertEquals(List.of(task1, task2), historyManager.getHistory());
    }

    @Test
    void shouldDeleteTasksToHistoryList() {
        Task task1 = new Task("Помыть машину", "Записаться на мойку или помыть машину самому",
                StatusTask.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("04.04.2025 11:00" , DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),1);
        Task task2 = new Task("Заехать в МФЦ", "Заехать в МФЦ за документами (окно №3)",
                StatusTask.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("04.04.2025 11:00" , DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")), 2);
        Task task3 = new Task("Заехать11111 в МФЦ", "Заехать в МФЦ за документами (окно №3)",
                StatusTask.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("04.04.2025 11:00" , DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")), 3);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task2.getId());
        assertEquals(List.of(task1, task3), historyManager.getHistory());
    }

    @Test
    void shouldReturnNullIfTaskIsEmpty() {
        historyManager.add(null);
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void validateRepeatInHistory() {
        Task task1 = new Task("Помыть машину", "Записаться на мойку или помыть машину самому",
                StatusTask.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("04.04.2025 11:00" , DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")), 1);
        historyManager.add(task1);
        historyManager.add(task1);
        historyManager.add(task1);
        assertEquals(List.of(task1), historyManager.getHistory());
    }

    @Test
    void validateDeleteFromStartOfHistory() {
        Task task1 = new Task("Помыть машину", "Записаться на мойку или помыть машину самому",
                StatusTask.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("04.04.2025 11:00" , DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")), 1);
        Task task2 = new Task("Заехать в МФЦ", "Заехать в МФЦ за документами (окно №3)",
                StatusTask.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("04.04.2025 11:00" , DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")), 2);
        Task task3 = new Task("Заехать11111 в МФЦ", "Заехать в МФЦ за документами (окно №3)",
                StatusTask.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("04.04.2025 11:00" , DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")), 3);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task1.getId());
        assertEquals(List.of(task2, task3), historyManager.getHistory());
    }

    @Test
    void validateDeleteFromEndOfHistory() {
        Task task1 = new Task("Помыть машину", "Записаться на мойку или помыть машину самому",
                StatusTask.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("04.04.2025 11:00" , DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")), 1);
        Task task2 = new Task("Заехать в МФЦ", "Заехать в МФЦ за документами (окно №3)",
                StatusTask.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("04.04.2025 11:00" , DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")), 2);
        Task task3 = new Task("Заехать11111 в МФЦ", "Заехать в МФЦ за документами (окно №3)",
                StatusTask.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("04.04.2025 11:00" , DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")), 3);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task3.getId());
        assertEquals(List.of(task1, task2), historyManager.getHistory());
    }

    @Test
    void validateDeleteFromMiddleOfHistory() {
        Task task1 = new Task("Помыть машину", "Записаться на мойку или помыть машину самому",
                StatusTask.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("04.04.2025 11:00" , DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")), 1);
        Task task2 = new Task("Заехать в МФЦ", "Заехать в МФЦ за документами (окно №3)",
                StatusTask.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("04.04.2025 11:00" , DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")), 2);
        Task task3 = new Task("Заехать11111 в МФЦ", "Заехать в МФЦ за документами (окно №3)",
                StatusTask.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("04.04.2025 11:00" , DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")), 3);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task2.getId());
        assertEquals(List.of(task1, task3), historyManager.getHistory());
    }
}