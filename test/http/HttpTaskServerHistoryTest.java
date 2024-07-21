package http;

import com.google.gson.Gson;
import managers.FileBackedTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskServerHistoryTest {
    HttpTaskServer server = new HttpTaskServer();
    FileBackedTaskManager manager = server.manager;
    Gson gson = server.gson;

    public HttpTaskServerHistoryTest() throws IOException {
    }

    @BeforeEach
    public void setUp() throws IOException, InterruptedException {
        server.start();

        Task task = new Task("t1", "test task");
        String taskJSON = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url1 = URI.create("http://localhost:8080/tasks");
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(url1)
                .POST(HttpRequest.BodyPublishers.ofString(taskJSON))
                .build();
        client.send(request1, HttpResponse.BodyHandlers.ofString());

        URI url2 = URI.create("http://localhost:8080/tasks/" + manager.getListOfTasks().get(0).getId());
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url2)
                .GET()
                .build();
        client.send(request2, HttpResponse.BodyHandlers.ofString());
    }

    @AfterEach
    public void shutDown() {
        manager.clearListOfTasks();
        server.stop();
    }

    @Test
    public void getHistoryTest() {
        assertEquals(1, manager.getHistory().size(),
                "Сервер не записал в историю обращение к задаче");
    }
}
