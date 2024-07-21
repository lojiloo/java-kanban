package httpAPI;

import com.google.gson.Gson;
import managers.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class HttpTaskServerTasksTest {
    HttpTaskServer server = new HttpTaskServer();
    FileBackedTaskManager manager = server.manager;
    Gson gson = server.gson;

    public HttpTaskServerTasksTest() throws IOException {
    }

    @BeforeEach
    public void setUp() throws IOException, InterruptedException {
        server.start();

        Task task = new Task("t1", "test task");
        String taskJSON = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJSON))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @AfterEach
    public void shutDown() {
        manager.clearListOfTasks();
        server.stop();
    }

    @Test
    public void addNewTaskTest() {
        assertNotNull(manager.getListOfTasks(),
                "Задачи не возвращаются");
        assertEquals(1, manager.getListOfTasks().size(),
                "Некорректное количество задач");
        assertEquals("t1", manager.getListOfTasks().get(0).getName(),
                "Некорректное имя задачи");
        assertEquals("test task", manager.getListOfTasks().get(0).getDescription(),
                "Некорректное описание задачи");
    }

    @Test
    public void updateTaskTest() throws IOException, InterruptedException {
        String requestBodyJSON = "{\"status\":\"" + Status.IN_PROGRESS + "\"}";

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + manager.getListOfTasks().get(0).getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(requestBodyJSON))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        assertEquals(manager.getListOfTasks().get(0).getStatus(), Status.IN_PROGRESS,
                "Статус не был изменён в результате запроса на сервер");
    }

    @Test
    public void updateTaskWithBusyTimeSlotTest() throws IOException, InterruptedException {
        String requestBodyJSON = "{\"startTime\":\"19.07.2024, 10:00\",\"duration\":\"60\"}";

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + manager.getListOfTasks().get(0).getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(requestBodyJSON))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Task task = new Task("t2", "test task");
        String requestBodyJSON2 = "{\"startTime\":\"19.07.2024, 10:30\",\"duration\":\"60\"}";
        String taskJSON = gson.toJson(task);

        URI url2 = URI.create("http://localhost:8080/tasks");
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url2)
                .POST(HttpRequest.BodyPublishers.ofString(taskJSON))
                .build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response2.statusCode());


        URI url3 = URI.create("http://localhost:8080/tasks/" + manager.getListOfTasks().get(1).getId());
        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(url3)
                .POST(HttpRequest.BodyPublishers.ofString(requestBodyJSON2))
                .build();
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response3.statusCode(),
                "Сервер не вернул код 406, когда задачи пересеклись по времени");
    }

    @Test
    public void getOneTaskTest() throws IOException, InterruptedException {
        Task task = manager.getListOfTasks().get(0);
        String taskJSON = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        assertEquals(taskJSON, response.body(),
                "Ответ сервера не совпадает с тем, что содержится в менеджере");
    }

    @Test
    public void getTaskThatDoesNotExistTest() throws IOException, InterruptedException {
        Task task = manager.getListOfTasks().get(0);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + (task.getId() + 1));
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(),
                "Сервер не вернул код 404, когда задача не была найдена");
    }

    @Test
    public void getAllTasksTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        assertEquals(gson.toJson(manager.getListOfTasks().get(0)), response.body(),
                "Ответ сервера не совпадает с тем, что содержится в менеджере");
    }

    @Test
    public void deleteOneTaskTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + manager.getListOfTasks().get(0).getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        assertEquals(0, manager.getListOfTasks().size(),
                "Задача не была удалена по айди");
    }

    @Test
    public void deleteAllTasksTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        assertEquals(0, manager.getListOfTasks().size(),
                "Список задач не пуст после удаления всех задач");
    }
}
