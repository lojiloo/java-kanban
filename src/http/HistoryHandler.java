package http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tasks.Task;

import java.io.IOException;
import java.util.List;

import static http.HttpTaskServer.gson;
import static http.HttpTaskServer.manager;

class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        List<Task> history = manager.getHistory();
        sendText(httpExchange, gson.toJson(history));
    }
}