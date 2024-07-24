package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.FileBackedTaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.List;

class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    FileBackedTaskManager manager;
    Gson gson;

    public PrioritizedHandler(FileBackedTaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        List<Task> prioritized = manager.getPrioritizedTasks();
        sendText(httpExchange, gson.toJson(prioritized));
    }

}
