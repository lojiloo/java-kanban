package http;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.FileBackedTaskManager;
import managers.TaskType;
import managers.TemporalException;
import tasks.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    FileBackedTaskManager manager;
    Gson gson;

    public SubtasksHandler(FileBackedTaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        String path = httpExchange.getRequestURI().getPath();
        InputStream requestBody = httpExchange.getRequestBody();

        switch (method) {
            case "GET":
                Optional<List<Subtask>> result = handleGet(path);
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
                    handlePost(requestBody, path);
                    sendText(httpExchange, "Сохранено");
                } catch (TemporalException e) {
                    sendHasInteraction(httpExchange, e);
                } catch (IllegalArgumentException e) {
                    sendHasProblemWithUpdate(httpExchange, e);
                }
                break;

            case "DELETE":
                handleDelete(path);
                sendText(httpExchange, "Удаление завершено успешно");
                break;
        }
    }

    protected Optional<List<Subtask>> handleGet(String path) {
        List<Subtask> result = null;
        String[] pathParts = path.split("/");

        if (pathParts.length == 2) {
            result = manager.getListOfSubtasks();
        } else if (pathParts.length == 3) {
            int id = Integer.parseInt(pathParts[2]);
            if (manager.getSubtaskById(id, false) != null) {
                result = new ArrayList<>(Collections.singletonList(manager.getSubtaskById(id)));
            }
        }

        return Optional.ofNullable(result);
    }

    protected void handlePost(InputStream inputStream, String path) throws IOException {
        String[] pathParts = path.split("/");

        if (pathParts.length > 2) {
            int id = Integer.parseInt(pathParts[2]);
            Subtask subtask = manager.getSubtaskById(id, false);
            manager.updateSubtask(toUpdateSubtask(subtask, inputStream));
        } else {
            manager.addNewSubtask(toAddSubtask(inputStream));
        }
    }

    protected void handleDelete(String path) {
        String[] pathParts = path.split("/");

        if (pathParts.length > 2) {
            int id = Integer.parseInt(pathParts[2]);
            manager.clearSubtasksById(id);
        } else {
            manager.clearListOfSubtasks();
        }
    }

    private Subtask toUpdateSubtask(Subtask subtask, InputStream inputStream) throws IOException {
        String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        checkUpdate(subtask, requestBody);
        return gson.fromJson(requestBody, Subtask.class);
    }

    private Subtask toAddSubtask(InputStream inputStream) throws IOException {
        String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        JsonElement jsonElement = JsonParser.parseString(requestBody);
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        String name = jsonObject.get("name").getAsString();
        String description = jsonObject.get("description").getAsString();
        int epicId = jsonObject.get("epicId").getAsInt();

        return new Subtask(name, description, epicId);
    }

    private void checkUpdate(Subtask subtask, String requestBody) {
        JsonElement jsonElement = JsonParser.parseString(requestBody);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        int id = jsonObject.get("id").getAsInt();
        if (subtask.getId() != id) {
            throw new IllegalArgumentException("id не может быть изменён");
        }
        TaskType type = TaskType.valueOf(jsonObject.get("type").getAsString());
        if (!type.equals(subtask.getType())) {
            throw new IllegalArgumentException("Тип задачи не может быть изменён");
        }
        int epicId = jsonObject.get("epicId").getAsInt();
        if (subtask.getEpicId() != epicId) {
            throw new IllegalArgumentException("id эпика для подзадачи не может быть изменён");
        }
    }

}
