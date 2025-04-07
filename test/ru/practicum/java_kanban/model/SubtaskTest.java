package ru.practicum.java_kanban.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    @Test
    public void shouldBeEqualEpicsById() {
        Epic epic1 = new Epic("Важный эпик 1", "очень очень очень важный список задач");
        Subtask subtask1 = new Subtask("Подзадача1", "Описание подзадачи1",
                StatusTask.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("04.04.2025 11:00" , DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")), epic1);
        Subtask subtask2 = new Subtask("Подзадача2", "Описание подзадачи2",
                StatusTask.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("04.04.2025 11:00" , DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")), epic1);
        subtask1.setId(subtask2.getId());
        Assertions.assertEquals(subtask1, subtask2);
    }


}