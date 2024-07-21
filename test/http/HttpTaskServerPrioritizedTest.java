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

public class HttpTaskServerPrioritizedTest {
    HttpTaskServer server = new HttpTaskServer();
    FileBackedTaskManager manager = server.manager;
    Gson gson = server.gson;

    public HttpTaskServerPrioritizedTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        server.start();
    }

    @AfterEach
    public void shutDown() {
        manager.clearListOfTasks();
        server.stop();
    }

    @Test
    public void getPrioritizedTasksTest() throws IOException, InterruptedException {
        Task task = new Task("t1", "test task");
        String taskJSON = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url1 = URI.create("http://localhost:8080/tasks");
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(url1)
                .POST(HttpRequest.BodyPublishers.ofString(taskJSON))
                .build();
        client.send(request1, HttpResponse.BodyHandlers.ofString());

        String requestBodyJSON = "{\"startTime\":\"19.07.2024, 10:00\",\"duration\":\"60\"}";
        URI url2 = URI.create("http://localhost:8080/tasks/" + manager.getListOfTasks().get(0).getId());
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url2)
                .POST(HttpRequest.BodyPublishers.ofString(requestBodyJSON))
                .build();
        client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(1, manager.getPrioritizedTasks().size(),
                "Сервер не записал задачу в список по времени");
    }

    @Test
    public void prioritizedTasksAreCorrectInTimeline() throws IOException, InterruptedException {
        Task task1 = new Task("t1", "test task");
        String taskJSON1 = gson.toJson(task1);

        HttpClient client = HttpClient.newHttpClient();
        URI url1 = URI.create("http://localhost:8080/tasks");
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(url1)
                .POST(HttpRequest.BodyPublishers.ofString(taskJSON1))
                .build();
        client.send(request1, HttpResponse.BodyHandlers.ofString());

        String requestBodyJSON1 = "{\"startTime\":\"19.07.2024, 10:00\",\"duration\":\"60\"}";
        URI url2 = URI.create("http://localhost:8080/tasks/" + manager.getListOfTasks().get(0).getId());
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url2)
                .POST(HttpRequest.BodyPublishers.ofString(requestBodyJSON1))
                .build();
        client.send(request2, HttpResponse.BodyHandlers.ofString());

        Task task2 = new Task("t1", "test task");
        String taskJSON2 = gson.toJson(task2);

        URI url3 = URI.create("http://localhost:8080/tasks");
        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(url3)
                .POST(HttpRequest.BodyPublishers.ofString(taskJSON2))
                .build();
        client.send(request3, HttpResponse.BodyHandlers.ofString());

        String requestBodyJSON2 = "{\"startTime\":\"19.07.2024, 11:30\",\"duration\":\"60\"}";
        URI url4 = URI.create("http://localhost:8080/tasks/" + manager.getListOfTasks().get(1).getId());
        HttpRequest request4 = HttpRequest.newBuilder()
                .uri(url4)
                .POST(HttpRequest.BodyPublishers.ofString(requestBodyJSON2))
                .build();
        client.send(request4, HttpResponse.BodyHandlers.ofString());

        assertEquals(task1, manager.getPrioritizedTasks().get(0),
                "Задача из наиболее раннего слота стоит не на первом месте в списке приоритетов");
        assertEquals(task2, manager.getPrioritizedTasks().get(1),
                "Задача из самого позднего слота стоит не на последнем месте в списке приоритетов");
    }
}
