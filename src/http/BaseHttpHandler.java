package http;

import com.sun.net.httpserver.HttpExchange;
import managers.TemporalException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler {

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendNotFound(HttpExchange h) throws IOException {
        byte[] resp = "Задача не была найдена".getBytes(StandardCharsets.UTF_8);
        h.sendResponseHeaders(404, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendHasInteraction(HttpExchange h, TemporalException e) throws IOException {
        byte[] resp = e.getMessage().getBytes(StandardCharsets.UTF_8);
        h.sendResponseHeaders(406, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendHasProblemWithUpdate(HttpExchange h, IllegalArgumentException e) throws IOException {
        byte[] resp = e.getMessage().getBytes(StandardCharsets.UTF_8);
        h.sendResponseHeaders(400, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }
}
