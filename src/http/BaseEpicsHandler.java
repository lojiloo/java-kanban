package http;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import managers.FileBackedTaskManager;
import tasks.Epic;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class BaseEpicsHandler extends BaseHttpHandler {

    protected static Optional<List<? extends Task>> handleGet(String path, FileBackedTaskManager manager) {
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

    protected void handlePost(InputStream inputStream, String path, FileBackedTaskManager manager) throws IOException {
        String[] pathParts = path.split("/");

        if (pathParts.length > 2) {
            throw new IllegalArgumentException("Поля эпика рассчитываются автоматически");
        } else {
            manager.addNewEpic(toAddEpic(inputStream));
        }
    }

    protected void handleDelete(String path, FileBackedTaskManager manager) {
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
