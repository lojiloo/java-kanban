package http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TemporalException;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import static http.HttpTaskServer.gson;
import static http.HttpTaskServer.manager;

public class TasksHandler extends BaseTasksHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        String path = httpExchange.getRequestURI().getPath();
        InputStream requestBody = httpExchange.getRequestBody();

        switch (method) {
            case "GET":
                Optional<List<Task>> result = handleGet(path, manager);
                if (result.isPresent()) {
                    List<Task> taskList = result.get();
                    if (taskList.size() == 1) {
                        Task task = taskList.get(0);
                        sendText(httpExchange, gson.toJson(task));
                    } else {
                        sendText(httpExchange, gson.toJson(taskList));
                    }
                } else {
                    sendNotFound(httpExchange);
                }
                break;

            case "POST":
                try {
                    handlePost(requestBody, path, manager);
                    sendText(httpExchange, "Сохранено");
                } catch (TemporalException e) {
                    sendHasInteraction(httpExchange, e);
                } catch (IllegalArgumentException e) {
                    sendHasProblemWithUpdate(httpExchange, e);
                }
                break;

            case "DELETE":
                handleDelete(path, manager);
                sendText(httpExchange, "Удаление завершено успешно");
                break;
        }
    }
}
