package ru.practicum.java_kanban.http;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import ru.practicum.java_kanban.model.Task;
import ru.practicum.java_kanban.service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class BaseHttpHandler {
    private TaskManager taskManager;
    private Gson gson;

    public BaseHttpHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public Gson getGson() {
        return gson;
    }


    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(200, resp.length);
        if (resp.length != 0)
            h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendGoodResponse(HttpExchange h) throws IOException {
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(201, 0);
        h.close();
    }


    protected void sendNotFound(HttpExchange h) throws IOException {
        byte[] resp = "Not Found".getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(404, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendHasInteractions(HttpExchange h) throws IOException {
        byte[] resp = "Not Acceptable".getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(406, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendInvalidId(HttpExchange h) throws IOException {
        byte[] resp = "Invalid Id".getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(400, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendUnknownMethod(HttpExchange h, String text) throws IOException {
        byte[] resp = "Unknown Method".getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(408, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendInternalError(HttpExchange h) throws IOException {
        byte[] resp = "Internal Server Error".getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(500, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendNotAllowedMethod(HttpExchange h) throws IOException {
        byte[] resp = "Not Allowed Method".getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(405, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }


    protected Optional<Integer> getId(HttpExchange exchange) {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        try {
            return Optional.of(Integer.parseInt(pathParts[2]));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    protected Endpoint getEndpoint(String requestPath, String requestMethod, String body) {
        String[] pathParts = requestPath.split("/");
        if (requestMethod.equals("GET")) {
            if (pathParts.length == 2 && pathParts[1].equals("history")) {
                return Endpoint.GET_HISTORY;
            }
            if (pathParts.length == 2 && pathParts[1].equals("prioritized")) {
                return Endpoint.GET_PRIORITIZED;
            }
            if (pathParts.length == 2) {
                return Endpoint.GET_ITEMS;
            }
            if (pathParts.length == 3) {
                return Endpoint.GET_ITEM;
            }
            if (pathParts.length == 4 && pathParts[3].equals("subtasks")) {
                return Endpoint.GET_SUBITEMS;
            }


        }
        if (requestMethod.equals("POST")) {
            JsonElement jsonElement = JsonParser.parseString(body);
            if (!jsonElement.isJsonObject()) { // проверяем, точно ли мы получили JSON-объект
                return Endpoint.UNKNOWN;
            }
            Task task = this.getGson().fromJson(body, new ru.practicum.java_kanban.http.TaskTypeToken().getType());
            if (task.getId() == null) {
                return Endpoint.CREATE_ITEM;
            } else {
                return Endpoint.UPDATE_ITEM;
            }
        }
        if (requestMethod.equals("DELETE")) {
            return Endpoint.DELETE_ITEM;
        }
        return Endpoint.UNKNOWN;
    }
}