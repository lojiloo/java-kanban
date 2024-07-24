package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpServer;
import managers.FileBackedTaskManager;
import managers.Managers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HttpTaskServer {
    static FileBackedTaskManager manager;
    static Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .setPrettyPrinting()
            .serializeNulls()
            .create();
    HttpServer httpTaskServer;

    public HttpTaskServer() throws IOException {
        manager = Managers.getDefault("file.txt");
        this.httpTaskServer = HttpServer.create(new InetSocketAddress(8080), 0);
        httpTaskServer.createContext("/tasks", new TasksHandler());
        httpTaskServer.createContext("/subtasks", new SubtasksHandler());
        httpTaskServer.createContext("/epics", new EpicsHandler());
        httpTaskServer.createContext("/history", new HistoryHandler());
        httpTaskServer.createContext("/prioritized", new PrioritizedHandler());
    }

    public HttpTaskServer(FileBackedTaskManager manager) throws IOException {
        this.httpTaskServer = HttpServer.create(new InetSocketAddress(8080), 0);
        HttpTaskServer.manager = manager;
        httpTaskServer.createContext("/tasks", new TasksHandler());
        httpTaskServer.createContext("/subtasks", new SubtasksHandler());
        httpTaskServer.createContext("/epics", new EpicsHandler());
        httpTaskServer.createContext("/history", new HistoryHandler());
        httpTaskServer.createContext("/prioritized", new PrioritizedHandler());
    }

    public void start() {
        httpTaskServer.start();
    }

    public void stop() {
        httpTaskServer.stop(1);
    }

    private static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
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

    private static class DurationAdapter extends TypeAdapter<Duration> {
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