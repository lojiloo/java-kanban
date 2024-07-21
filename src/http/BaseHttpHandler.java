package http;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import managers.*;
import tasks.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class BaseHttpHandler {

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendNotFound(HttpExchange h) throws IOException {
        byte[] resp = "Задача не была найдена".getBytes(StandardCharsets.UTF_8);
        h.sendResponseHeaders(404, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendHasInteraction(HttpExchange h, TemporalException e) throws IOException {
        byte[] resp = e.getMessage().getBytes(StandardCharsets.UTF_8);
        h.sendResponseHeaders(406, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    // ---------- методы для обработки запросов в соответствии с типом задачи ----------

    protected Optional<List<? extends Task>> handleGet(String path, FileBackedTaskManager manager) {
        TaskType type = returnType(path);
        List<? extends Task> result = null;

        String[] pathParts = path.split("/");
        if (pathParts.length == 2) {
            switch (type) {
                case TASK:
                    result = manager.getListOfTasks();
                    break;
                case SUBTASK:
                    result = manager.getListOfSubtasks();
                    break;
                case EPIC:
                    result = manager.getListOfEpics();
                    break;
            }
        } else if (pathParts.length == 3) {
            int id = Integer.parseInt(pathParts[2]);
            switch (type) {
                case TASK:
                    if (manager.getTaskById(id, false) != null) {
                        result = new ArrayList<>(Collections.singletonList(manager.getTaskById(id)));
                    }
                    break;
                case SUBTASK:
                    if (manager.getSubtaskById(id, false) != null) {
                        result = new ArrayList<>(Collections.singletonList(manager.getSubtaskById(id)));
                    }
                    break;
                case EPIC:
                    if (manager.getEpicById(id, false) != null) {
                        result = new ArrayList<>(Collections.singletonList(manager.getEpicById(id)));
                    }
                    break;
            }
        } else if (pathParts.length == 4) {
            int id = Integer.parseInt(pathParts[2]);
            if (manager.getEpicById(id, false) != null) {
                result = manager.getSubtasksByEpic(manager.getEpicById(id, false));
            }
        }
        return Optional.ofNullable(result);
    }

    protected void handlePost(InputStream inputStream, String path, FileBackedTaskManager manager) throws IOException {
        TaskType type = returnType(path);
        String[] pathParts = path.split("/");

        if (pathParts.length > 2) {
            int id = Integer.parseInt(pathParts[2]);
            switch (type) {
                case TASK:
                    Task task = manager.getTaskById(id, false);
                    manager.updateTask(toUpdateTask(task, inputStream));
                    break;
                case SUBTASK:
                    Subtask subtask = manager.getSubtaskById(id, false);
                    manager.updateSubtask(toUpdateSubtask(subtask, inputStream));
                    break;
                case EPIC:
                    Epic epic = manager.getEpicById(id, false);
                    manager.updateEpic(toUpdateEpic(epic, inputStream));
                    break;
            }
        } else {
            switch (type) {
                case TASK:
                    manager.addNewTask(toAddTask(inputStream));
                    break;
                case SUBTASK:
                    manager.addNewSubtask(toAddSubtask(inputStream));
                    break;
                case EPIC:
                    manager.addNewEpic(toAddEpic(inputStream));
                    break;
            }
        }
    }

    protected void handleDelete(String path, FileBackedTaskManager manager) {
        TaskType type = returnType(path);
        String[] pathParts = path.split("/");

        if (pathParts.length > 2) {
            int id = Integer.parseInt(pathParts[2]);

            switch (type) {
                case TASK:
                    manager.clearTasksById(id);
                    break;
                case SUBTASK:
                    manager.clearSubtasksById(id);
                    break;
                case EPIC:
                    manager.clearEpicsById(id);
                    break;
            }
        } else {
            switch (type) {
                case TASK:
                    manager.clearListOfTasks();
                    break;
                case SUBTASK:
                    manager.clearListOfSubtasks();
                case EPIC:
                    manager.clearListOfEpics();
            }
        }

    }

    // ---------- вспомогательные методы для обработки запросов с POST ----------

    private TaskType returnType(String path) {
        String[] pathParts = path.split("/");
        String st = pathParts[1];

        return TaskType.valueOf(st.substring(0, st.length() - 1).toUpperCase());
    }

    private Task toUpdateTask(Task task, InputStream inputStream) throws IOException {
        String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        JsonElement jsonElement = JsonParser.parseString(requestBody);
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        if (jsonObject.has("name")) {
            String newName = jsonObject.get("name").getAsString();
            task.setName(newName);
        }
        if (jsonObject.has("description")) {
            String newDescription = jsonObject.get("description").getAsString();
            task.setDescription(newDescription);
        }
        if (jsonObject.has("status")) {
            Status newStatus = Status.valueOf(jsonObject.get("status").getAsString());
            task.setStatus(newStatus);
        }
        if (jsonObject.has("startTime")) {
            String st = jsonObject.get("startTime").getAsString();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
            LocalDateTime startTime = LocalDateTime.parse(st, dtf);

            long d = jsonObject.get("duration").getAsLong();
            Duration duration = Duration.ofMinutes(d);
            task.setTemporal(startTime, duration);
        }

        return task;
    }

    private Task toAddTask(InputStream inputStream) throws IOException {
        String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        JsonElement jsonElement = JsonParser.parseString(requestBody);
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        String name = jsonObject.get("name").getAsString();
        String description = jsonObject.get("description").getAsString();

        return new Task(name, description);

    }

    private Subtask toUpdateSubtask(Subtask subtask, InputStream inputStream) throws IOException {
        String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        JsonElement jsonElement = JsonParser.parseString(requestBody);
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        if (jsonObject.has("name")) {
            String newName = jsonObject.get("name").getAsString();
            subtask.setName(newName);
        }
        if (jsonObject.has("description")) {
            String newDescription = jsonObject.get("description").getAsString();
            subtask.setDescription(newDescription);
        }
        if (jsonObject.has("status")) {
            Status newStatus = Status.valueOf(jsonObject.get("status").getAsString());
            subtask.setStatus(newStatus);
        }
        if (jsonObject.has("startTime")) {
            String st = jsonObject.get("startTime").getAsString();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
            LocalDateTime startTime = LocalDateTime.parse(st, dtf);

            long d = jsonObject.get("duration").getAsLong();
            Duration duration = Duration.ofMinutes(d);
            subtask.setTemporal(startTime, duration);
        }

        return subtask;
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

    private Epic toUpdateEpic(Epic epic, InputStream inputStream) throws IOException {
        String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        JsonElement jsonElement = JsonParser.parseString(requestBody);
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        if (jsonObject.has("name")) {
            String newName = jsonObject.get("name").getAsString();
            epic.setName(newName);
        }
        if (jsonObject.has("description")) {
            String newDescription = jsonObject.get("description").getAsString();
            epic.setDescription(newDescription);
        }

        return epic;
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
