package ru.practicum.java_kanban.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.practicum.java_kanban.service.TaskManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler {
    public PrioritizedHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handleGetPrioritized(HttpExchange exchange) throws IOException {
        String response = this.getGson().toJson(this.getTaskManager().getPrioritizedTasks());
        sendText(exchange, response);
    }
}
