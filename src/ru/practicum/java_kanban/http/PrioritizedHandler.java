package ru.practicum.java_kanban.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.practicum.java_kanban.service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    public PrioritizedHandler(TaskManager taskManager, Gson gson) {
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
                case GET_PRIORITIZED:
                    handleGetPrioritized(httpExchange);
                    break;
                default:
                    response = "Вы использовали какой-то другой метод!";
                    sendUnknownMethod(httpExchange, response);
            }
        } catch (Exception e) {
            sendInternalError(httpExchange);
        }
    }

    public void handleGetPrioritized(HttpExchange exchange) throws IOException {
        String response = this.getGson().toJson(this.getTaskManager().getPrioritizedTasks());
        sendText(exchange, response);
    }
}
