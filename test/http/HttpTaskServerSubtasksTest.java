package http;

import managers.FileBackedTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static http.HttpTaskServer.gson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskServerSubtasksTest {
    FileBackedTaskManager manager = new FileBackedTaskManager("file.txt");
    HttpTaskServer server = new HttpTaskServer(manager);

    public HttpTaskServerSubtasksTest() throws IOException {
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

        Subtask subtask = new Subtask("s1", "test subtask", manager.getListOfEpics().get(0).getId());
        String subtaskJSON = gson.toJson(subtask);
        URI url2 = URI.create("http://localhost:8080/subtasks");
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url2)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJSON))
                .build();
        client.send(request2, HttpResponse.BodyHandlers.ofString());
    }

    @AfterEach
    public void shutDown() {
        manager.clearListOfSubtasks();
        manager.clearListOfEpics();
        server.stop();
    }

    @Test
    public void addNewSubtaskTest() {
        assertNotNull(manager.getListOfSubtasks(),
                "Подзадачи не возвращаются");
        assertEquals(1, manager.getListOfSubtasks().size(),
                "Некорректное количество подзадач");
        assertEquals("s1", manager.getListOfSubtasks().get(0).getName(),
                "Некорректное имя подзадачи");
        assertEquals("test subtask", manager.getListOfSubtasks().get(0).getDescription(),
                "Некорректное описание подзадачи");
    }

    @Test
    public void updateSubtaskTest() throws IOException, InterruptedException {
        String requestBodyJSON = "{\"id\":2," + "\"epicId\":\"1\"," +
                "\"name\":\"s1\"," +
                "\"status\":\"IN_PROGRESS\"," +
                "\"description\":\"test subtask\"," +
                "\"type\":\"SUBTASK\"," +
                "\"startTime\":null," +
                "\"duration\":null}";

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + manager.getListOfSubtasks().get(0).getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(requestBodyJSON))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        assertEquals(manager.getListOfSubtasks().get(0).getStatus(), Status.IN_PROGRESS,
                "Статус не был изменён в результате запроса на сервер");
    }

    @Test
    public void updateSubtaskWithBusyTimeSlotTest() throws IOException, InterruptedException {
        String requestBodyJSON = "{\"id\":2," + "\"epicId\":\"1\"," +
                "\"name\":\"s1\"," +
                "\"status\":\"NEW\"," +
                "\"description\":\"test subtask\"," +
                "\"type\":\"SUBTASK\"," +
                "\"startTime\":\"19.07.2024, 10:00\"," +
                "\"duration\":\"60\"}";

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + manager.getListOfSubtasks().get(0).getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(requestBodyJSON))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Subtask subtask = new Subtask("s2", "test subtask", manager.getListOfEpics().get(0).getId());
        String requestBodyJSON2 = "{\"id\":3," + "\"epicId\":\"1\"," +
                "\"name\":\"s2\"," +
                "\"status\":\"NEW\"," +
                "\"description\":\"test subtask\"," +
                "\"type\":\"SUBTASK\"," +
                "\"startTime\":\"19.07.2024, 10:30\"," +
                "\"duration\":\"60\"}";
        String subtaskJSON = gson.toJson(subtask);

        URI url2 = URI.create("http://localhost:8080/subtasks");
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url2)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJSON))
                .build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response2.statusCode());


        URI url3 = URI.create("http://localhost:8080/subtasks/" + manager.getListOfSubtasks().get(1).getId());
        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(url3)
                .POST(HttpRequest.BodyPublishers.ofString(requestBodyJSON2))
                .build();
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response3.statusCode(),
                "Сервер не вернул код 406, когда подзадачи пересеклись по времени");
    }

    @Test
    public void getOneSubtaskTest() throws IOException, InterruptedException {
        Subtask subtask = manager.getListOfSubtasks().get(0);
        String subtaskJSON = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subtask.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        assertEquals(subtaskJSON, response.body(),
                "Ответ сервера не совпадает с тем, что содержится в менеджере");
    }

    @Test
    public void getSubtaskThatDoesNotExistTest() throws IOException, InterruptedException {
        Subtask subtask = manager.getListOfSubtasks().get(0);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + (subtask.getId() + 1));
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(),
                "Сервер не вернул код 404, когда подзадача не была найдена");
    }

    @Test
    public void getAllSubtasksTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        assertEquals(gson.toJson(manager.getListOfSubtasks().get(0)), response.body(),
                "Ответ сервера не совпадает с тем, что содержится в менеджере");
    }

    @Test
    public void deleteOneSubtaskTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + manager.getListOfSubtasks().get(0).getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        assertEquals(0, manager.getListOfSubtasks().size(),
                "Подзадача не была удалена по айди");
    }

    @Test
    public void deleteAllSubtasksTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        assertEquals(0, manager.getListOfSubtasks().size(),
                "Список подзадач не пуст после удаления всех подзадач");
    }
}
