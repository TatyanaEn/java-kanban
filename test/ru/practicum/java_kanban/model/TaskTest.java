package ru.practicum.java_kanban.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    public void shouldBeEqualTasksById() {
        Task task1 = new Task("Помыть машину", "Записаться на мойку или помыть машину самому", StatusTask.NEW,
                Duration.ofMinutes(30),
                LocalDateTime.parse("04.04.2025 11:00" , DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
        Task task2 = new Task("Заехать в МФЦ", "Заехать в МФЦ за документами (окно №3)", StatusTask.NEW,
                Duration.ofMinutes(30),
                LocalDateTime.parse("04.04.2025 11:00" , DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
        task1.setId(task2.getId());
        Assertions.assertEquals(task1, task2);
    }


}