package ru.practicum.java_kanban.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import ru.practicum.java_kanban.TypeAdapter.DurationTypeAdapter;
import ru.practicum.java_kanban.TypeAdapter.LocalDateTimeTypeAdapter;
import ru.practicum.java_kanban.service.Managers;
import ru.practicum.java_kanban.service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static HttpServer httpServer;
    private static TaskManager taskManager;
    private static Gson gson;


    public HttpTaskServer() {
        this.taskManager = (TaskManager) Managers.getDefault();
    }

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;
    }


    public static void start() throws IOException {
        if (taskManager == null)
            taskManager = (TaskManager) Managers.getDefault();
        LocalDateTimeTypeAdapter localDateTimeTypeAdapter = new LocalDateTimeTypeAdapter();
        DurationTypeAdapter durationTypeAdapter = new DurationTypeAdapter();
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, localDateTimeTypeAdapter.nullSafe())
                .registerTypeAdapter(Duration.class, durationTypeAdapter)
                .create();

        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0); // связываем сервер с сетевым портом
        httpServer.createContext("/tasks", new TaskHandler(taskManager, gson)); // связываем путь и обработчик
        httpServer.createContext("/subtasks", new SubtaskHandler(taskManager, gson)); // связываем путь и обработчик
        httpServer.createContext("/epics", new EpicHandler(taskManager, gson)); // связываем путь и обработчик
        httpServer.createContext("/history", new HistoryHandler(taskManager, gson)); // связываем путь и обработчик
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager, gson)); // связываем путь и обработчик
        httpServer.start(); // запускаем сервер

        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");

    }

    public static void main(String[] args) throws IOException {
        start();
    }


    public void stop() {
        httpServer.stop(1);
    }

    public Gson getGson() {
        return this.gson;
    }

}
