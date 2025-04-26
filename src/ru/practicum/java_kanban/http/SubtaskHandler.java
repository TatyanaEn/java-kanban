package ru.practicum.java_kanban.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.practicum.java_kanban.model.Subtask;
import ru.practicum.java_kanban.service.TaskManager;

import java.io.IOException;
import java.util.Optional;

public class SubtaskHandler extends AbstractTaskHandler {

    public SubtaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handleGetItems(HttpExchange exchange) throws IOException {
        String response = this.getGson().toJson(this.getTaskManager().getSubtasks());
        sendText(exchange, response);
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
    public void handleUpdateItem(HttpExchange exchange, String body) throws IOException {
        Subtask parsedTask = this.getGson().fromJson(body, new SubtaskTypeToken().getType());
        Boolean result = this.getTaskManager().updateSubtask(parsedTask);
        if (result) {
            sendGoodResponse(exchange);
        } else
            sendHasInteractions(exchange);
    }


}
