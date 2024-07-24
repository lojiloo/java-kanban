package http;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import managers.FileBackedTaskManager;
import managers.TaskType;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static http.HttpTaskServer.gson;

public class BaseTasksHandler extends BaseHttpHandler {

    protected Optional<List<Task>> handleGet(String path, FileBackedTaskManager manager) {
        List<Task> result = null;
        String[] pathParts = path.split("/");
        if (pathParts.length == 2) {
            result = manager.getListOfTasks();
        } else if (pathParts.length == 3) {
            int id = Integer.parseInt(pathParts[2]);
            if (manager.getTaskById(id, false) != null) {
                result = new ArrayList<>(Collections.singletonList(manager.getTaskById(id)));
            }
        }
        return Optional.ofNullable(result);
    }

    protected void handlePost(InputStream inputStream, String path, FileBackedTaskManager manager) throws IOException {
        String[] pathParts = path.split("/");

        if (pathParts.length > 2) {
            int id = Integer.parseInt(pathParts[2]);
            Task task = manager.getTaskById(id, false);
            manager.updateTask(toUpdateTask(task, inputStream));
        } else {
            manager.addNewTask(toAddTask(inputStream));
        }
    }

    protected void handleDelete(String path, FileBackedTaskManager manager) {
        String[] pathParts = path.split("/");

        if (pathParts.length > 2) {
            int id = Integer.parseInt(pathParts[2]);
            manager.clearTasksById(id);
        } else {
            manager.clearListOfTasks();
        }
    }

    private Task toUpdateTask(Task task, InputStream inputStream) throws IOException {
        String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        checkUpdate(task, requestBody);
        return gson.fromJson(requestBody, Task.class);
    }

    private Task toAddTask(InputStream inputStream) throws IOException {
        String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        JsonElement jsonElement = JsonParser.parseString(requestBody);
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        String name = jsonObject.get("name").getAsString();
        String description = jsonObject.get("description").getAsString();

        return new Task(name, description);
    }

    private void checkUpdate(Task task, String requestBody) {
        JsonElement jsonElement = JsonParser.parseString(requestBody);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        int id = jsonObject.get("id").getAsInt();
        TaskType type = TaskType.valueOf(jsonObject.get("type").getAsString());

        if (id != task.getId()) {
            throw new IllegalArgumentException("id не может быть изменён");
        }
        if (!type.equals(task.getType())) {
            throw new IllegalArgumentException("Тип задачи не может быть изменён");
        }
    }

}
