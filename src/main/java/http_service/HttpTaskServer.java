package http_service;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import constants.Status;
import manager.Managers;
import manager.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.util.HashMap;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {
    public static final int PORT = 8080;
    private final HttpServer httpServer;
    private final Gson gson;
    private final TaskManager taskManager;

    public static void main(String[] args) throws IOException {
        new HttpTaskServer().start();

        HttpClient client = HttpClient.newHttpClient();
        Task task = new Task("TASK", "TASK_DESCRIPTION", Status.NEW);
        URL url = new URL("http://localhost:8080/tasks");
        HashMap<String, Integer> test = new HashMap<>();
        int testId = 25;
        test.put(url.getFile(), testId);

        System.out.println(test);

//        URI url = URI.create("http://localhost:8080/tasks/task/");
//
//
//        Gson gson = new Gson();
//        String json = gson.toJson(task);
//        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
//        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
//        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpTaskServer() throws IOException {
        taskManager = Managers.getDefaultTask();
        gson = Managers.getGson();
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", this::handler);
    }

    private void handler(HttpExchange exchange) {
        URI uri = exchange.getRequestURI();
        String path = uri.getPath();
        String query = uri.getQuery();
        String method = exchange.getRequestMethod();
        String body = String.valueOf(exchange.getRequestBody());
        String[] pathParts = path.split("/");

        try {
            switch (method) {
                case "GET":
                    if (pathParts[1].equals("tasks") && pathParts.length == 2) {
                        if (exchange.getRequestBody() != null) {
                            taskManager.setUrl(new URL("http://localhost/tasks"));
                            taskManager.getPrioritizedTasks();
                            handlerGetPrioritizedTasks(exchange);
                        }
                        break;
                    }
                    if (pathParts[1].equals("tasks")
                            && pathParts[2].equals("task")
                            && query == null
                            && pathParts.length == 3) {

                        break;
                    }
                    if (pathParts[2].equals("task")) {
                        assert query != null;
                        if (query.equals("id") && pathParts.length == 3) {

                            break;
                        }
                    }
                    if (pathParts[2].equals("subtask") && query == null) {

                        break;
                    }
                    if (pathParts[2].equals("subtask") && query.equals("id") && pathParts.length == 3) {

                        break;
                    }
                    if (pathParts[2].equals("subtask") && pathParts[3].equals("epic")) {

                        break;
                    }
                    if (pathParts[2].equals("epic") && query == null) {

                        break;
                    }
                    if (pathParts[2].equals("epic") && query.equals("id") && pathParts.length == 3) {

                        break;
                    }
                    if (pathParts[2].equals("history")) {

                        break;
                    }
                case "POST": // Методы добавления задачи
                    if (pathParts[2].equals("task")) {
                        if (query == null) {
                            writeErrorResponse(exchange, 400);
                        }

                        break;
                    }
                    if (pathParts[2].equals("epic")) {

                        break;
                    }
                    if (pathParts[2].equals("subtask")) {

                        break;
                    }
                case "DELETE":
                    if (pathParts[2].equals("task") && query == null) {

                        break;
                    }
                    if (pathParts[2].equals("epic") && query == null) {

                        break;
                    }
                    if (pathParts[2].equals("subtask") && query == null) {

                        break;
                    }
                    if (pathParts[2].equals("task") && query.equals("id")) {

                        break;
                    }
                    if (pathParts[2].equals("epic") && query.equals("id")) {

                        break;
                    }
                    if (pathParts[2].equals("subtask") && query.equals("id")) {

                        break;
                    }
            }
        } catch (ArrayIndexOutOfBoundsException | MalformedURLException e) {
            writeErrorResponse(exchange, 404);
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

    private void writeErrorResponse(HttpExchange exchange, int responseCode) {
        String response = "";

        try {
            if (response.isBlank()) {
                exchange.sendResponseHeaders(responseCode, 0);
            } else {
                switch (responseCode) {
                    case 400:
                        response = "В запросе содержится ошибка. Проверьте параметры и повторите запрос.";
                        break;
                    case 404:
                        response = "По указанному адресу нет ресурса. Проверьте URL-адрес ресурса и повторите запрос.";
                        break;
                    case 422:
                        response = "Команды, содержащиеся в запросе не могут быть выполнены.";
                        break;
                    case 500:
                        response = "На стороне сервера произошла непредвиденная ошибка.";
                        break;
                }
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                byte[] bytes = response.getBytes(UTF_8);
                exchange.sendResponseHeaders(responseCode, bytes.length);
                exchange.getResponseBody().write(bytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            exchange.close();
        }
    }

    private void writeSuccessResponse(HttpExchange exchange, String response) {
        byte[] resp = response.getBytes(UTF_8);

        try {
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, resp.length);
            exchange.getResponseBody().write(resp);
        } catch (IOException e) {
            writeErrorResponse(exchange, 500);
        } finally {
            exchange.close();
        }
    }

    private void handlerGetPrioritizedTasks(HttpExchange exchange) {
        String response = gson.toJson(taskManager.getPrioritizedTasks());
        writeSuccessResponse(exchange, response);
    }
}