package ru.practicum.java_kanban.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.practicum.java_kanban.model.Subtask;
import ru.practicum.java_kanban.service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {

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

    public SubtaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    public void handleGetItems(HttpExchange exchange) throws IOException {
        String response = this.getGson().toJson(this.getTaskManager().getSubtasks());
        sendText(exchange, response);
    }

    public void handleGetItem(HttpExchange exchange) throws IOException {
        Optional<Integer> idOpt = getId(exchange);
        if (idOpt.isEmpty()) {
            sendInvalidId(exchange);
            return;
        }
        int taskId = idOpt.get();
        Subtask subtask = this.getTaskManager().getSubtaskById(taskId);
        if (subtask != null) {
            String response = this.getGson().toJson(subtask);
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
        int subtaskId = idOpt.get();
        this.getTaskManager().deleteSubtaskById(subtaskId);
        sendText(exchange, "");

    }

    public void handleCreateItem(HttpExchange exchange, String body) throws IOException {
        try {
            Subtask parsedTask = this.getGson().fromJson(body, new SubtaskTypeToken().getType());
            Integer idSubtask = this.getTaskManager().createSubtask(parsedTask);
            if (idSubtask != -1) {
                sendText(exchange, idSubtask.toString());
            } else
                sendHasInteractions(exchange);
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    public void handleUpdateItem(HttpExchange exchange, String body) throws IOException {
        Subtask parsedTask = this.getGson().fromJson(body, new SubtaskTypeToken().getType());
        Boolean result = this.getTaskManager().updateSubtask(parsedTask);
        if (result) {
            sendGoodResponse(exchange);
        } else
            sendHasInteractions(exchange);
    }


}
