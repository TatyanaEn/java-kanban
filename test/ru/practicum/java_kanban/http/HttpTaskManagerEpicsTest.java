package ru.practicum.java_kanban.http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HttpTaskManagerEpicsTest {

    // создаём экземпляр FileBackedEpicManager
    String fileName = System.getProperty("user.home") + File.separator + "listEpic.csv";
    File file = new File(fileName);
    FileBackedTaskManager manager = new FileBackedTaskManager(fileName);
    HttpTaskServer epicServer = new HttpTaskServer(manager);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    Gson gson = null;

    public HttpTaskManagerEpicsTest() throws IOException {
    }

    @BeforeEach
    public void setUp() throws IOException {
        manager.deleteEpics();
        manager.deleteSubtasks();
        manager.deleteEpics();
        epicServer.start();
        gson = epicServer.getGson();
    }

    @AfterEach
    public void shutDown() {
        epicServer.stop();
    }

    // вспомогательный класс для определения типа коллекции и типа её элементов
    class SubtaskListTypeToken extends TypeToken<List<Subtask>> {
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("Epic 2", "Testing epic 2");
        // конвертируем её в JSON
        String epicJson = gson.toJson(epic);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        Assertions.assertEquals(200, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Epic> epicsFromManager = manager.getEpics();

        Assertions.assertNotNull(epicsFromManager, "Задачи не возвращаются");
        Assertions.assertEquals(1, epicsFromManager.size(), "Некорректное количество задач");
        Assertions.assertEquals("Epic 2", epicsFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateEpic() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("Test 2", "Testing epic 2");
        int idEpic = manager.createEpic(epic);

        Epic newEpic = manager.getEpicById(idEpic);
        newEpic.setName("Test 3");

        String epicJson = gson.toJson(newEpic);

        //отправляем задачу на обновление
        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        // вызываем рест, отвечающий за об оьновление задачи
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        Assertions.assertEquals(201, response.statusCode());
        // проверяем, что создалась одна задача с корректным именем
        List<Epic> epicsFromManager = manager.getEpics();

        Assertions.assertNotNull(epicsFromManager, "Задачи не возвращаются");
        Assertions.assertEquals(1, epicsFromManager.size(), "Некорректное количество задач");
        Assertions.assertEquals("Test 3", epicsFromManager.get(0).getName(), "Некорректное имя задачи");

    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("Test 2", "Testing epic 2");
        int idEpic = manager.createEpic(epic);

        //удаляем задачу по ИД
        HttpClient client = HttpClient.newHttpClient();

        URI get_url = URI.create("http://localhost:8080/epics/" + idEpic);
        HttpRequest request = HttpRequest.newBuilder().uri(get_url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // проверяем код ответа
        Assertions.assertEquals(200, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Epic> epicsFromManager = manager.getEpics();

        Assertions.assertNotNull(epicsFromManager, "Задачи не возвращаются");
        Assertions.assertEquals(0, epicsFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void testGetSubtaskFromEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Важный эпик 1", "очень очень очень важный список задач");
        int idEpic = manager.createEpic(epic);
        // создаём задачу
        Subtask subtask1 = new Subtask("Test 1", "Testing Subtask 1",
                StatusTask.NEW, Duration.ofMinutes(5), LocalDateTime.now(), manager.getEpicById(idEpic));
        int idSubtask1 = manager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask("Test 2", "Testing Subtask 2",
                StatusTask.NEW, Duration.ofMinutes(5), LocalDateTime.parse("04.04.2025 15:00", DATE_TIME_FORMATTER), manager.getEpicById(idEpic));
        int idSubtask2 = manager.createSubtask(subtask2);


        //отправляем задачу на обновление
        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/epics/" + idEpic + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за об оьновление задачи
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        Assertions.assertEquals(200, response.statusCode());

        if (response.statusCode() == 200) {
            List<Subtask> list = gson.fromJson(response.body(), new SubtaskListTypeToken().getType());
            // проверяем, что создалась одна задача с корректным именем

            Assertions.assertEquals(2, list.size(), "Некорректное количество задач");
            Assertions.assertEquals("Test 2", list.get(1).getName(), "Некорректное имя задачи");

        }


    }

}
