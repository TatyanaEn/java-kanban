package ru.practicum.java_kanban.http;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public interface IBaseHttpHandler {
    public void handleCreateItem(HttpExchange exchange, String body) throws IOException;

    public void handleUpdateItem(HttpExchange exchange, String body) throws IOException;

    public void handleGetItems(HttpExchange exchange) throws IOException;

    public void handleGetItem(HttpExchange exchange) throws IOException;

    public void handleDeleteItem(HttpExchange exchange) throws IOException;

    public void handleGetSubitems(HttpExchange exchange) throws IOException;

    public void handleGetHistory(HttpExchange exchange) throws IOException;

    public void handleGetPrioritized(HttpExchange exchange) throws IOException;
}
