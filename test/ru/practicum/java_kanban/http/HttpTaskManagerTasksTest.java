package ru.practicum.java_kanban.http;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.java_kanban.model.StatusTask;
import ru.practicum.java_kanban.model.Task;
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

public class HttpTaskManagerTasksTest {

    // создаём экземпляр FileBackedTaskManager
    String fileName = System.getProperty("user.home") + File.separator + "listTask.csv";
    File file = new File(fileName);
    FileBackedTaskManager manager = new FileBackedTaskManager(fileName);
    HttpTaskServer taskServer = new HttpTaskServer(manager);

    Gson gson = null;

    public HttpTaskManagerTasksTest() throws IOException {
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
    public void testAddTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2",
                StatusTask.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        // конвертируем её в JSON
        String taskJson = gson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        Assertions.assertEquals(200, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = manager.getTasks();

        Assertions.assertNotNull(tasksFromManager, "Задачи не возвращаются");
        Assertions.assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        Assertions.assertEquals("Test 2", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2",
                StatusTask.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        int idTask = manager.createTask(task);

        Task newTask = manager.getTaskById(idTask);
        newTask.setName("Test 3");

        String taskJson = gson.toJson(newTask);

        //отправляем задачу на обновление
        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за об оьновление задачи
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        Assertions.assertEquals(201, response.statusCode());
        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = manager.getTasks();

        Assertions.assertNotNull(tasksFromManager, "Задачи не возвращаются");
        Assertions.assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        Assertions.assertEquals("Test 3", tasksFromManager.get(0).getName(), "Некорректное имя задачи");

    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2",
                StatusTask.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        int idTask = manager.createTask(task);

        //удаляем задачу по ИД
        HttpClient client = HttpClient.newHttpClient();

        URI get_url = URI.create("http://localhost:8080/tasks/" + idTask);
        HttpRequest request = HttpRequest.newBuilder().uri(get_url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // проверяем код ответа
        Assertions.assertEquals(200, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = manager.getTasks();

        Assertions.assertNotNull(tasksFromManager, "Задачи не возвращаются");
        Assertions.assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");
    }

}