package ru.practicum.java_kanban.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import ru.practicum.java_kanban.model.StatusTask;
import ru.practicum.java_kanban.model.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private static InMemoryHistoryManager historyManager;


    @BeforeAll
    static void beforeAll() {
        historyManager = (InMemoryHistoryManager) Managers.getDefaultHistory();
    }
    @Test
    void shouldAddTasksToHistoryList() {
        Task task1 = new Task("Помыть машину", "Записаться на мойку или помыть машину самому",
                StatusTask.NEW);
        Task task2 = new Task("Заехать в МФЦ", "Заехать в МФЦ за документами (окно №3)",
                StatusTask.NEW);
        historyManager.add(task1);
        historyManager.add(task2);

        assertEquals(List.of(task1, task2), historyManager.getHistory());
    }

    @Test
    void shouldReturnNullIfTaskIsEmpty() {
        historyManager.add(null);
        assertTrue(historyManager.getHistory().isEmpty());
    }
}