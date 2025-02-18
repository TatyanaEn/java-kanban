package ru.practicum.java_kanban.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    public void shouldBeEqualEpicsById() {
        Epic epic1 = new Epic("Важный эпик 1", "очень очень очень важный список задач");
        Epic epic2 = new Epic("Важный эпик 2", "111111111");
        epic1.setId(epic2.getId());
        Assertions.assertEquals(epic1, epic2);
    }


}