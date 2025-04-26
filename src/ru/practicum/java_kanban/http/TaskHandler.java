package ru.practicum.java_kanban.http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import ru.practicum.java_kanban.model.Task;
import ru.practicum.java_kanban.service.TaskManager;

import java.io.IOException;
import java.util.Optional;

public class TaskHandler extends AbstractTaskHandler implements IBaseHttpHandler {

    public TaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handleGetItems(HttpExchange exchange) throws IOException {
        String response = this.getGson().toJson(this.getTaskManager().getTasks());
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
        Task task = this.getTaskManager().getTaskById(taskId);
        if (task != null) {
            String response = this.getGson().toJson(task);
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
        int taskId = idOpt.get();
        this.getTaskManager().deleteTaskById(taskId);
        sendText(exchange, "");

    }

    class TaskTypeToken extends TypeToken<Task> {

    }

    @Override
    public void handleCreateItem(HttpExchange exchange, String body) throws IOException {
        Task parsedTask = this.getGson().fromJson(body, new TaskTypeToken().getType());
        Integer idTask = -1;
        idTask = this.getTaskManager().createTask((Task) parsedTask);
        if (idTask != -1) {
            sendText(exchange, idTask.toString());
        } else
            sendHasInteractions(exchange);
    }

    @Override
    public void handleUpdateItem(HttpExchange exchange, String body) throws IOException {
        Task parsedTask = this.getGson().fromJson(body, new TaskTypeToken().getType());
        Boolean result = this.getTaskManager().updateTask((Task) parsedTask);
        if (result) {
            sendGoodResponse(exchange);
        } else
            sendHasInteractions(exchange);
    }

}