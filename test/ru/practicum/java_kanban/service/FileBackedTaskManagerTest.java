package ru.practicum.java_kanban.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.java_kanban.exception.ManagerSaveException;

import java.io.File;

public class FileBackedTaskManagerTest  extends TaskManagerTest<FileBackedTaskManager>{

    @BeforeEach
    void BeforeEach() {
        String fileName = System.getProperty("user.home") + File.separator + "listTask.csv";
        taskManager = new FileBackedTaskManager(fileName);
    }

    @Test
    public void testException() {
        Assertions.assertThrows(ManagerSaveException.class, () -> {
            String fileName = System.getProperty("user.home") + File.separator + "listTask1111.csv";
            taskManager = new FileBackedTaskManager(fileName);
            File file = new File(fileName);
            taskManager.loadFromFile(file);
        }, "Попытка прочитать несуществующий файл должна привести к исключению");
    }
}
