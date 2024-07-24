package http;

import managers.FileBackedTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static http.HttpTaskServer.gson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskServerEpicsTest {
    FileBackedTaskManager manager = new FileBackedTaskManager("file.txt");
    HttpTaskServer server = new HttpTaskServer(manager);

    public HttpTaskServerEpicsTest() throws IOException {
    }

    @BeforeEach
    public void setUp() throws IOException, InterruptedException {
        server.start();

        Epic epic = new Epic("e1", "test epic");
        String epicJSON = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url1 = URI.create("http://localhost:8080/epics");
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(url1)
                .POST(HttpRequest.BodyPublishers.ofString(epicJSON))
                .build();
        client.send(request1, HttpResponse.BodyHandlers.ofString());
    }

    @AfterEach
    public void shutDown() {
        manager.clearListOfEpics();
        server.stop();
    }

    @Test
    public void addNewEpicTest() throws IOException, InterruptedException {
        assertNotNull(manager.getListOfEpics(),
                "Эпики не возвращаются");
        assertEquals(1, manager.getListOfEpics().size(),
                "Некорректное количество эпиков");
        assertEquals("e1", manager.getListOfEpics().get(0).getName(),
                "Некорректное имя эпика");
        assertEquals("test epic", manager.getListOfEpics().get(0).getDescription(),
                "Некорректное описание эпика");
    }

    @Test
    public void getOneEpicTest() throws IOException, InterruptedException {
        Epic epic = manager.getListOfEpics().get(0);
        String epicJSON = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epic.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        assertEquals(epicJSON, response.body(),
                "Ответ сервера не совпадает с тем, что содержится в менеджере");
    }

    @Test
    public void getEpicThatDoesNotExistTest() throws IOException, InterruptedException {
        Epic epic = manager.getListOfEpics().get(0);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + (epic.getId() + 1));
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(),
                "Сервер не вернул код 404, когда эпик не был найден");
    }

    @Test
    public void getSubtasksByEpicTest() throws IOException, InterruptedException {
        Subtask sub1 = new Subtask("s1", "test subtask", manager.getListOfEpics().get(0).getId());
        String sub1JSON = gson.toJson(sub1);

        HttpClient client = HttpClient.newHttpClient();
        URI url1 = URI.create("http://localhost:8080/subtasks");
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(url1)
                .POST(HttpRequest.BodyPublishers.ofString(sub1JSON))
                .build();
        client.send(request1, HttpResponse.BodyHandlers.ofString());

        URI url2 = URI.create("http://localhost:8080/epics/" + manager.getListOfEpics().get(0).getId() + "/subtasks");
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url2)
                .GET()
                .build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(response2.body(), gson.toJson(manager.getSubtasksByEpic(manager.getListOfEpics().get(0)).get(0)),
                "Сервер вернул для эпика список подзадач, отличающийся от того, что предоставил менеджер");
    }

    @Test
    public void getSubtasksByEpicThatDoesNotExistTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url2 = URI.create("http://localhost:8080/epics/" + (manager.getListOfEpics().get(0).getId() + 1) + "/subtasks");
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url2)
                .GET()
                .build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response2.statusCode(),
                "Сервер не вернул код 404, когда эпик не был найден");
    }

    @Test
    public void getAllEpicsTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        assertEquals(gson.toJson(manager.getListOfEpics().get(0)), response.body(),
                "Ответ сервера не совпадает с тем, что содержится в менеджере");
    }

    @Test
    public void deleteOneEpicTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + manager.getListOfEpics().get(0).getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        assertEquals(0, manager.getListOfEpics().size(),
                "Эпик не был удалён по айди");
    }

    @Test
    public void deleteAllEpicsTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        assertEquals(0, manager.getListOfEpics().size(),
                "Список эпиков не пуст после удаления всех эпиков");
    }
}