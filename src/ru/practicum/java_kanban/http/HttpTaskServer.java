package ru.practicum.java_kanban.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import ru.practicum.java_kanban.TypeAdapter.DurationTypeAdapter;
import ru.practicum.java_kanban.TypeAdapter.LocalDateTimeTypeAdapter;
import ru.practicum.java_kanban.model.Task;
import ru.practicum.java_kanban.service.FileBackedTaskManager;
import ru.practicum.java_kanban.service.InMemoryTaskManager;
import ru.practicum.java_kanban.service.Managers;
import ru.practicum.java_kanban.service.TaskManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.stream.Collectors;

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
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0); // связываем сервер с сетевым портом
        httpServer.createContext("/tasks", new TaskHandler(taskManager)); // связываем путь и обработчик
        LocalDateTimeTypeAdapter localDateTimeTypeAdapter = new LocalDateTimeTypeAdapter();
        DurationTypeAdapter durationTypeAdapter = new DurationTypeAdapter();
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, localDateTimeTypeAdapter)
                .registerTypeAdapter(Duration.class, durationTypeAdapter)
                .create();
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

    static class TaskHandler extends BaseHttpHandler {

        public TaskHandler(TaskManager taskManager) {
            super(taskManager);
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            System.out.println("Началась обработка /tasks запроса от клиента.");
            String response;

            // извлеките метод из запроса
            String method = httpExchange.getRequestMethod();

            String[] pathParts = httpExchange.getRequestURI().getPath().split("/");
            Endpoint endpoint = getEndpoint(httpExchange.getRequestURI().getPath(), method);
            switch(endpoint) {
                case CREATE_TASK:
                    handleCreateTask(httpExchange);
                    break;
                case UPDATE_TASK:
                    handleUpdateTask(httpExchange);
                    break;
                case GET_TASKS:
                    handleGetTasks(httpExchange);
                    break;
                case GET_TASK:
                    handleGetTask(httpExchange);
                    break;
                // не забудьте про ответ для остальных методов
                default:
                    response = "Вы использовали какой-то другой метод!";
                    sendText(httpExchange, response);
            }
        }

        private void handleGetTasks(HttpExchange exchange) throws IOException {
            String response = gson.toJson(taskManager.getTasks());
            sendText(exchange, response);
        }

        private void handleGetTask(HttpExchange exchange) throws IOException {
            Optional<Integer> idOpt = getId(exchange);
            if(idOpt.isEmpty()) {
                sendInvalidId(exchange);
                return;
            }
            int taskId = idOpt.get();

            Task task = taskManager.getTaskById(taskId);
            if (task != null) {
                String response = gson.toJson(task);
                sendText(exchange, response);
                return;
            }

            sendNotFound(exchange);
        }

        class TaskTypeToken extends TypeToken<Task> {

        }

        private void handleCreateTask(HttpExchange exchange) throws IOException {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

            Task parsedTask = gson.fromJson(body, new TaskTypeToken().getType());
            Integer idTask = taskManager.createTask(parsedTask);
            if (idTask != -1) {
                sendGoodResponse(exchange);
            }
            else
                sendHasInteractions(exchange);
        }

        private void handleUpdateTask(HttpExchange exchange) throws IOException {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

            Task parsedTask = gson.fromJson(body, new TaskTypeToken().getType());
            Boolean result = taskManager.updateTask(parsedTask);
            if (result) {
                sendGoodResponse(exchange);
            }
            else
                sendHasInteractions(exchange);
        }

        private Optional<Integer> getId(HttpExchange exchange) {
            String[] pathParts = exchange.getRequestURI().getPath().split("/");
            try {
                return Optional.of(Integer.parseInt(pathParts[2]));
            } catch (NumberFormatException exception) {
                return Optional.empty();
            }
        }

        private Endpoint getEndpoint(String requestPath, String requestMethod) {
            String[] pathParts = requestPath.split("/");
            if (requestMethod.equals("GET")) {
                if (pathParts.length == 2 && pathParts[1].equals("tasks")) {
                    return Endpoint.GET_TASKS;
                }
                if (pathParts.length == 3 && pathParts[1].equals("tasks")) {
                    return Endpoint.GET_TASK;
                }
            }
            if (requestMethod.equals("POST")) {
                if (pathParts.length == 2 && pathParts[1].equals("tasks")) {
                    return Endpoint.CREATE_TASK;
                }
                if (pathParts.length == 3 && pathParts[1].equals("tasks")) {
                    return Endpoint.UPDATE_TASK;
                }
            }
            return Endpoint.UNKNOWN;
        }
    }
}
