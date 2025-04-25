package ru.practicum.java_kanban.http;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.java_kanban.model.Epic;
import ru.practicum.java_kanban.model.StatusTask;
import ru.practicum.java_kanban.model.Subtask;
import ru.practicum.java_kanban.service.FileBackedTaskManager;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class HttpTaskManagerSubtasksTest {


    // создаём экземпляр FileBackedTaskManager
    String fileName = System.getProperty("user.home") + File.separator + "listTask.csv";
    File file = new File(fileName);
    FileBackedTaskManager manager = new FileBackedTaskManager(fileName);
    HttpTaskServer taskServer = new HttpTaskServer(manager);

    Gson gson = null;

    public HttpTaskManagerSubtasksTest() throws IOException {
    }

    @BeforeEach
    public void setUp() throws IOException {
        manager.deleteTasks();
        manager.deleteSubtasks();
        manager.deleteEpics();
        taskServer.start();
        gson = taskServer.getGson();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Важный эпик 1", "очень очень очень важный список задач");
        int idEpic = manager.createEpic(epic);
        // создаём задачу
        Subtask subtask = new Subtask("Test 2", "Testing Subtask 2",
                StatusTask.NEW, Duration.ofMinutes(5), LocalDateTime.now(), manager.getEpicById(idEpic));
        // конвертируем её в JSON
        String taskJson = gson.toJson(subtask);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        Assertions.assertEquals(200, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Subtask> tasksFromManager = manager.getSubtasks();

        Assertions.assertNotNull(tasksFromManager, "Задачи не возвращаются");
        Assertions.assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        Assertions.assertEquals("Test 2", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateSubtask() throws IOException, InterruptedException {

        Epic epic = new Epic("Важный эпик 1", "очень очень очень важный список задач");
        int idEpic = manager.createEpic(epic);
        // создаём задачу
        Subtask subtask = new Subtask("Test 2", "Testing Subtask 2",
                StatusTask.NEW, Duration.ofMinutes(5), LocalDateTime.now(), manager.getEpicById(idEpic));
        int idSubtask = manager.createSubtask(subtask);

        Subtask newSubtask = manager.getSubtaskById(idSubtask);
        //меняем подзадачу
        newSubtask.setName("Test 3");
        // конвертируем её в JSON
        String taskJson = gson.toJson(newSubtask);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        // обновляем подзадачу
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за об добавление задачи
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(201, response.statusCode());
        // проверяем, что создалась одна задача с корректным именем
        List<Subtask> tasksFromManager = manager.getSubtasks();

        Assertions.assertNotNull(tasksFromManager, "Задачи не возвращаются");
        Assertions.assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        Assertions.assertEquals("Test 3", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testDeleteSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Важный эпик 1", "очень очень очень важный список задач");
        int idEpic = manager.createEpic(epic);
        // создаём задачу
        Subtask subtask = new Subtask("Test 2", "Testing Subtask 2",
                StatusTask.NEW, Duration.ofMinutes(5), LocalDateTime.now(), manager.getEpicById(idEpic));
        int idSubtask = manager.createSubtask(subtask);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        //удаляем задачу по ИД
        URI url = URI.create("http://localhost:8080/subtasks/" + idSubtask);

        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        // вызываем рест, отвечающий за об добавление задачи
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // проверяем код ответа
        Assertions.assertEquals(200, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Subtask> tasksFromManager = manager.getSubtasks();

        Assertions.assertNotNull(tasksFromManager, "Задачи не возвращаются");
        Assertions.assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");
    }

}
