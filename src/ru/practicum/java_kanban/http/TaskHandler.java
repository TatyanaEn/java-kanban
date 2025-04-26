package ru.practicum.java_kanban.http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.practicum.java_kanban.model.Task;
import ru.practicum.java_kanban.service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    public TaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            System.out.println("Началась обработка  запроса от клиента.");
            String response;

            // извлеките метод из запроса
            String method = httpExchange.getRequestMethod();
            String body = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            String[] pathParts = httpExchange.getRequestURI().getPath().split("/");
            Endpoint endpoint = getEndpoint(httpExchange.getRequestURI().getPath(), method, body);
            switch (endpoint) {
                case CREATE_ITEM:
                    handleCreateItem(httpExchange, body);
                    break;
                case UPDATE_ITEM:
                    handleUpdateItem(httpExchange, body);
                    break;
                case GET_ITEMS:
                    handleGetItems(httpExchange);
                    break;
                case GET_ITEM:
                    handleGetItem(httpExchange);
                    break;
                case DELETE_ITEM:
                    handleDeleteItem(httpExchange);
                    break;
                default:
                    response = "Вы использовали какой-то другой метод!";
                    sendUnknownMethod(httpExchange, response);
            }
        } catch (Exception e) {
            sendInternalError(httpExchange);
        }
    }

    public void handleGetItems(HttpExchange exchange) throws IOException {
        String response = this.getGson().toJson(this.getTaskManager().getTasks());
        sendText(exchange, response);
    }

    public void handleGetItem(HttpExchange exchange) throws IOException {
        Optional<Integer> idOpt = getId(exchange);
        if (idOpt.isEmpty()) {
            sendInvalidId(exchange);
            return;
        }
        int taskId = idOpt.get();
        Task task = this.getTaskManager().getTaskById(taskId);
        if (task != null) {
            String response = this.getGson().toJson(task);
            sendText(exchange, response);
            return;
        }

        sendNotFound(exchange);
    }

    public void handleDeleteItem(HttpExchange exchange) throws IOException {
        Optional<Integer> idOpt = getId(exchange);
        if (idOpt.isEmpty()) {
            sendInvalidId(exchange);
            return;
        }
        int taskId = idOpt.get();
        this.getTaskManager().deleteTaskById(taskId);
        sendText(exchange, "");

    }

    class TaskTypeToken extends TypeToken<Task> {

    }

    public void handleCreateItem(HttpExchange exchange, String body) throws IOException {
        Task parsedTask = this.getGson().fromJson(body, new TaskTypeToken().getType());
        Integer idTask = -1;
        idTask = this.getTaskManager().createTask((Task) parsedTask);
        if (idTask != -1) {
            sendText(exchange, idTask.toString());
        } else
            sendHasInteractions(exchange);
    }

    public void handleUpdateItem(HttpExchange exchange, String body) throws IOException {
        Task parsedTask = this.getGson().fromJson(body, new TaskTypeToken().getType());
        Boolean result = this.getTaskManager().updateTask((Task) parsedTask);
        if (result) {
            sendGoodResponse(exchange);
        } else
            sendHasInteractions(exchange);
    }

}