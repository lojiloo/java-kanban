package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpServer;
import managers.FileBackedTaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HttpTaskServer {
    private final FileBackedTaskManager manager;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .setPrettyPrinting()
            .serializeNulls()
            .create();
    private final HttpServer httpTaskServer;

    public HttpTaskServer(FileBackedTaskManager manager) throws IOException {
        this.httpTaskServer = HttpServer.create(new InetSocketAddress(8080), 0);
        this.manager = manager;
        httpTaskServer.createContext("/tasks", new TasksHandler(this.manager, this.gson));
        httpTaskServer.createContext("/subtasks", new SubtasksHandler(this.manager, this.gson));
        httpTaskServer.createContext("/epics", new EpicsHandler(this.manager, this.gson));
        httpTaskServer.createContext("/history", new HistoryHandler(this.manager, this.gson));
        httpTaskServer.createContext("/prioritized", new PrioritizedHandler(this.manager, this.gson));
    }

    public void start() {
        httpTaskServer.start();
    }

    public void stop() {
        httpTaskServer.stop(1);
    }

    public Gson getGson() {
        return gson;
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