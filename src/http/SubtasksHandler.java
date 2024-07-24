package http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TemporalException;
import tasks.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import static http.HttpTaskServer.gson;
import static http.HttpTaskServer.manager;

class SubtasksHandler extends BaseSubtasksHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        String path = httpExchange.getRequestURI().getPath();
        InputStream requestBody = httpExchange.getRequestBody();

        switch (method) {
            case "GET":
                Optional<List<Subtask>> result = handleGet(path, manager);
                if (result.isPresent()) {
                    List<Subtask> subtaskList = result.get();
                    if (subtaskList.size() == 1) {
                        Subtask subtask = subtaskList.get(0);
                        sendText(httpExchange, gson.toJson(subtask));
                    } else {
                        sendText(httpExchange, gson.toJson(subtaskList));
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
