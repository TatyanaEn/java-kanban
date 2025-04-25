package ru.practicum.java_kanban.http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import java.time.format.DateTimeFormatter;
import java.util.Set;

public class HttpTaskManagerPrioritizedTest {


    // создаём экземпляр FileBackedEpicManager
    String fileName = System.getProperty("user.home") + File.separator + "listEpic.csv";
    File file = new File(fileName);
    FileBackedTaskManager manager = new FileBackedTaskManager(fileName);
    HttpTaskServer epicServer = new HttpTaskServer(manager);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    Gson gson = null;

    public HttpTaskManagerPrioritizedTest() throws IOException {
    }

    class TaskSetTypeToken extends TypeToken<Set<Task>> {
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

    @Test
    public void testPrioritized() throws IOException, InterruptedException {
        // создаём задачу
        Task task1 = new Task("Test 1", "Testing task 1",
                StatusTask.NEW, Duration.ofMinutes(5), LocalDateTime.parse("10.04.2025 15:00", DATE_TIME_FORMATTER));
        Integer id1 = manager.createTask(task1);

        Task task2 = new Task("Test 2", "Testing task 2",
                StatusTask.NEW, Duration.ofMinutes(5), LocalDateTime.parse("04.04.2025 15:00", DATE_TIME_FORMATTER));
        Integer id2 = manager.createTask(task2);


        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за об оьновление задачи
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        Assertions.assertEquals(200, response.statusCode());

        if (response.statusCode() == 200) {
            Set<Task> list = gson.fromJson(response.body(), new TaskSetTypeToken().getType());
            // проверяем, что создалась одна задача с корректным именем
            Assertions.assertEquals(2, list.size(), "Некорректное количество задач");

        }
    }
}
