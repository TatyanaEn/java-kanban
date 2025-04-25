package ru.practicum.java_kanban.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.practicum.java_kanban.model.Epic;
import ru.practicum.java_kanban.model.Subtask;
import ru.practicum.java_kanban.service.TaskManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

public class EpicHandler extends BaseHttpHandler {

    public EpicHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handleGetItems(HttpExchange exchange) throws IOException {
        String response = this.getGson().toJson(this.getTaskManager().getEpics());
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
        Epic epic = this.getTaskManager().getEpicById(taskId);
        if (epic != null) {
            String response = this.getGson().toJson(epic);
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
        int epicId = idOpt.get();
        this.getTaskManager().deleteEpicById(epicId);
        sendText(exchange, "");

    }

    @Override
    public void handleCreateItem(HttpExchange exchange, String body) throws IOException {
        Epic parsedTask = this.getGson().fromJson(body, new EpicTypeToken().getType());
        Integer idEpic = this.getTaskManager().createEpic(parsedTask);
        if (idEpic != -1) {
            sendText(exchange, idEpic.toString());
        } else
            sendHasInteractions(exchange);
    }

    @Override
    public void handleUpdateItem(HttpExchange exchange, String body) throws IOException {
        Epic parsedTask = this.getGson().fromJson(body, new EpicTypeToken().getType());
        Boolean result = this.getTaskManager().updateEpic(parsedTask);
        if (result) {
            sendGoodResponse(exchange);
        } else
            sendHasInteractions(exchange);
    }

    @Override
    public void handleGetSubitems(HttpExchange exchange) throws IOException {
        Optional<Integer> idOpt = getId(exchange);
        if (idOpt.isEmpty()) {
            sendInvalidId(exchange);
            return;
        }
        int epicId = idOpt.get();
        Epic epic = this.getTaskManager().getEpicById(epicId);
        if (epic != null) {
            ArrayList<Subtask> subtasks = this.getTaskManager().getSubtasksFromEpic(epic);
            if (subtasks.size() != 0) {
                String response = this.getGson().toJson(subtasks);
                sendText(exchange, response);
                return;
            }
        }
        sendNotFound(exchange);
    }
}
