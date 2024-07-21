package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import managers.*;
import tasks.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class HttpTaskServer {
    HttpServer httpTaskServer;
    FileBackedTaskManager manager;
    Gson gson;

    public HttpTaskServer() throws IOException {
        this.httpTaskServer = HttpServer.create(new InetSocketAddress(8080), 0);
        httpTaskServer.createContext("/tasks", new TasksHandler());
        httpTaskServer.createContext("/subtasks", new SubtasksHandler());
        httpTaskServer.createContext("/epics", new EpicsHandler());
        httpTaskServer.createContext("/history", new HistoryHandler());
        httpTaskServer.createContext("/prioritized", new PrioritizedHandler());

        this.manager = Managers.getDefault("file.txt");
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .setPrettyPrinting()
                .serializeNulls()
                .create();
    }

    public void start() {
        httpTaskServer.start();
    }

    public void stop() {
        httpTaskServer.stop(1);
    }

    class TasksHandler extends BaseHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String method = httpExchange.getRequestMethod();
            String path = httpExchange.getRequestURI().getPath();
            InputStream requestBody = httpExchange.getRequestBody();

            switch (method) {
                case "GET":
                    Optional<List<? extends Task>> result = handleGet(path, manager);
                    if (result.isPresent()) {
                        if (result.get().size() == 1) {
                            Task task = result.get().get(0);
                            sendText(httpExchange, gson.toJson(task));
                        } else {
                            List<? extends Task> taskList = result.get();
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
                    }
                    break;

                case "DELETE":
                    handleDelete(path, manager);
                    sendText(httpExchange, "Удаление завершено успешно");
                    break;
            }
        }
    }

    class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String method = httpExchange.getRequestMethod();
            String path = httpExchange.getRequestURI().getPath();
            InputStream requestBody = httpExchange.getRequestBody();

            switch (method) {
                case "GET":
                    Optional<List<? extends Task>> result = handleGet(path, manager);
                    if (result.isPresent()) {
                        if (result.get().size() == 1) {
                            Subtask subtask = (Subtask) result.get().get(0);
                            sendText(httpExchange, gson.toJson(subtask));
                        } else {
                            List<? extends Task> subtaskList = result.get();
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
                    }
                    break;

                case "DELETE":
                    handleDelete(path, manager);
                    sendText(httpExchange, "Удаление завершено успешно");
                    break;
            }
        }
    }

    class EpicsHandler extends BaseHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String method = httpExchange.getRequestMethod();
            String path = httpExchange.getRequestURI().getPath();
            InputStream requestBody = httpExchange.getRequestBody();

            switch (method) {
                case "GET":
                    Optional<List<? extends Task>> result = handleGet(path, manager);
                    if (result.isPresent()) {
                        if (result.get().size() == 1) {
                            Task epic = result.get().get(0);
                            sendText(httpExchange, gson.toJson(epic));
                        } else {
                            List<? extends Task> epicList = result.get();
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
                    }

                    break;

                case "DELETE":
                    handleDelete(path, manager);
                    sendText(httpExchange, "Удаление завершено успешно");
                    break;
            }
        }
    }

    class HistoryHandler extends BaseHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            List<Task> history = manager.getHistory();
            sendText(httpExchange, gson.toJson(history));
        }
    }

    class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            List<Task> prioritized = manager.getPrioritizedTasks();
            sendText(httpExchange, gson.toJson(prioritized));
        }
    }

    private class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");

        @Override
        public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
            if (localDateTime == null) {
                jsonWriter.nullValue();
            } else {
                jsonWriter.value(localDateTime.format(dtf));
            }
        }

        @Override
        public LocalDateTime read(JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull();
                return null;
            } else {
                return LocalDateTime.parse(jsonReader.nextString(), dtf);
            }
        }
    }

    private class DurationAdapter extends TypeAdapter<Duration> {
        @Override
        public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
            if (duration == null) {
                jsonWriter.nullValue();
            } else {
                jsonWriter.value(duration.toMinutes());
            }
        }

        @Override
        public Duration read(JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull();
                return null;
            } else {
                return Duration.ofMinutes(jsonReader.nextLong());
            }
        }
    }
}