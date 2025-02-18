package ru.practicum.java_kanban.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ManagersTest {

    @Test
    public void testNewManager() {

        InMemoryTaskManager newManager = (InMemoryTaskManager) Managers.getDefault();
        Assertions.assertNotNull(newManager);

        InMemoryHistoryManager newHistoryManager = (InMemoryHistoryManager) Managers.getDefaultHistory();
        Assertions.assertNotNull(newHistoryManager);

    }

}