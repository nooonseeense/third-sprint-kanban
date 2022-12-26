package http_service;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {
    public static final int PORT = 8080;
    private final HttpServer httpServer;
    private final Gson gson;
    private final TaskManager taskManager;

    public static void main(String[] args) throws IOException {
        new HttpTaskServer().start();
    }

    public HttpTaskServer() throws IOException {
        taskManager = Managers.getDefaultTask();
        gson = Managers.getGson();
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", this::handler);
    }

    private void handler(HttpExchange exchange) throws IOException { // TODO Сделать try catch + switch case
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();
        String[] pathParts = path.split("/");

        switch (method) {
            case "GET":
                if (pathParts[1].equals("tasks")) {

                }
                if (pathParts[1].equals("tasks") && pathParts[2].equals("task")) {

                }
                if (pathParts[2].equals("task") && pathParts[3].equals("?id=")) {

                }
                if (pathParts[2].equals("subtask")) {

                }
                if (pathParts[2].equals("subtask") && pathParts[3].equals("?id=")) {

                }
                if (pathParts[2].equals("epic")) {

                }
                if (pathParts[2].equals("epic") && pathParts[3].equals("?id=")) {

                }
                if (pathParts[2].equals("subtask") && pathParts[3].equals("epic")) {

                }
                if (pathParts[2].equals("history")) {

                }
                break;
            case "POST":
                if (pathParts[2].equals("task")) {

                }
                if (pathParts[2].equals("epic")) {

                }
                if (pathParts[2].equals("subtask")) {

                }
                break;
            case "DELETE":
                if (pathParts[2].equals("task")) {

                }
                if (pathParts[2].equals("epic")) {

                }
                if (pathParts[2].equals("subtask")) {

                }
                if (pathParts[2].equals("task") && pathParts[3].equals("?id=")) {

                }
                if (pathParts[2].equals("epic") && pathParts[3].equals("?id=")) {

                }
                if (pathParts[2].equals("subtask") && pathParts[3].equals("?id=")) {

                }
                break;
            default:
                writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }
    }

    public void start() {
        System.out.println("Started TaskServer in PORT: " + PORT);
        System.out.println("http://localhost:" + PORT + "/tasks/");
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("Server stopped in PORT: " + PORT);
    }

    private String readText(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes(), UTF_8);
    }

    private void writeResponse(HttpExchange exchange,
                               String responseString,
                               int responseCode) throws IOException {
        if (responseString.isBlank()) {
            exchange.sendResponseHeaders(responseCode, 0);
        } else {
            byte[] bytes = responseString.getBytes(UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(responseCode, bytes.length);
            exchange.getResponseBody().write(bytes);
        }
        exchange.close();
    }
}