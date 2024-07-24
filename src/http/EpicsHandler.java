package http;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.FileBackedTaskManager;
import managers.TemporalException;
import tasks.Epic;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    FileBackedTaskManager manager;
    Gson gson;

    public EpicsHandler(FileBackedTaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    private static Optional<List<? extends Task>> handleGet(String path, FileBackedTaskManager manager) {
        List<? extends Task> result = null;
        String[] pathParts = path.split("/");

        if (pathParts.length == 2) {
            result = manager.getListOfEpics();
        } else if (pathParts.length == 3) {
            int id = Integer.parseInt(pathParts[2]);
            if (manager.getEpicById(id, false) != null) {
                result = new ArrayList<>(Collections.singletonList(manager.getEpicById(id)));
            }
        } else if (pathParts.length == 4) {
            int id = Integer.parseInt(pathParts[2]);
            if (manager.getEpicById(id, false) != null) {
                result = manager.getSubtasksByEpic(manager.getEpicById(id, false));
            }
        }
        return Optional.ofNullable(result);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        String path = httpExchange.getRequestURI().getPath();
        InputStream requestBody = httpExchange.getRequestBody();

        switch (method) {
            case "GET":
                Optional<List<? extends Task>> result = handleGet(path, manager);
                if (result.isPresent()) {
                    List<? extends Task> epicList = result.get();
                    if (epicList.size() == 1) {
                        Task epic = epicList.get(0);
                        sendText(httpExchange, gson.toJson(epic));
                    } else {
                        sendText(httpExchange, gson.toJson(epicList));
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

    private void handlePost(InputStream inputStream, String path, FileBackedTaskManager manager) throws IOException {
        String[] pathParts = path.split("/");

        if (pathParts.length > 2) {
            throw new IllegalArgumentException("Поля эпика рассчитываются автоматически");
        } else {
            manager.addNewEpic(toAddEpic(inputStream));
        }
    }

    private void handleDelete(String path, FileBackedTaskManager manager) {
        String[] pathParts = path.split("/");

        if (pathParts.length > 2) {
            int id = Integer.parseInt(pathParts[2]);
            manager.clearEpicsById(id);
        } else {
            manager.clearListOfEpics();
        }
    }

    private Epic toAddEpic(InputStream inputStream) throws IOException {
        String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        JsonElement jsonElement = JsonParser.parseString(requestBody);
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        String name = jsonObject.get("name").getAsString();
        String description = jsonObject.get("description").getAsString();

        return new Epic(name, description);
    }
}