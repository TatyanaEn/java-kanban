package ru.practicum.java_kanban.http;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.practicum.java_kanban.model.Task;
import ru.practicum.java_kanban.service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

abstract class AbstractTaskHandler extends BaseHttpHandler implements IBaseHttpHandler, HttpHandler {

    public AbstractTaskHandler(TaskManager taskManager, Gson gson) {
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
                case GET_SUBITEMS:
                    handleGetSubitems(httpExchange);
                    break;
                case GET_HISTORY:
                    handleGetHistory(httpExchange);
                    break;
                case GET_PRIORITIZED:
                    handleGetPrioritized(httpExchange);
                    break;
                // не забудьте про ответ для остальных методов
                default:
                    response = "Вы использовали какой-то другой метод!";
                    sendUnknownMethod(httpExchange, response);
            }
        } catch (Exception e) {
            sendInternalError(httpExchange);
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

    public void handleGetPrioritized(HttpExchange httpExchange) throws IOException {
        sendNotAllowedMethod(httpExchange);
    }

    ;

    public void handleGetHistory(HttpExchange httpExchange) throws IOException {
        sendNotAllowedMethod(httpExchange);
    }

    ;

    public void handleGetSubitems(HttpExchange httpExchange) throws IOException {
        sendNotAllowedMethod(httpExchange);
    }

    ;

    public void handleDeleteItem(HttpExchange httpExchange) throws IOException {
        sendNotAllowedMethod(httpExchange);
    }

    ;

    public void handleGetItem(HttpExchange httpExchange) throws IOException {
        sendNotAllowedMethod(httpExchange);
    }

    ;

    public void handleGetItems(HttpExchange httpExchange) throws IOException {
        sendNotAllowedMethod(httpExchange);
    }

    ;

    public void handleUpdateItem(HttpExchange httpExchange, String body) throws IOException {
        sendNotAllowedMethod(httpExchange);
    }

    ;

    public void handleCreateItem(HttpExchange httpExchange, String body) throws IOException {
        sendNotAllowedMethod(httpExchange);
    }

    ;


}
