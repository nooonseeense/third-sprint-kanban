package http_service;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import constants.Status;
import constants.TaskType;
import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;

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

    private void handler(HttpExchange exchange) throws IOException {
        try {
            URI uri = exchange.getRequestURI();
            String path = uri.getPath();
            String query = uri.getQuery();
            String method = exchange.getRequestMethod();
            String body = readText(exchange);
            String[] pathParts = path.split("/");
            String response;
            String idInString;

            switch (method) {
                case "GET":
                    if (pathParts[1].equals("tasks") && pathParts.length == 2) {
                        if (exchange.getRequestBody() != null) {
                            response = String.valueOf(taskManager.getPrioritizedTasks());
                            sendText(exchange, response, 200);
                        }
                        break;
                    }
                    if (pathParts[1].equals("tasks")
                            && pathParts[2].equals("task")
                            && query == null
                            && pathParts.length == 3) {
                        response = gson.toJson(taskManager.getTasks()); // tyt
                        sendText(exchange, response, 200);
                        break;
                    }
                    if (pathParts[2].equals("task")) {
                        assert query != null;
                        if (query.equals("id") && pathParts.length == 3) {
                            idInString = query.replaceFirst("^id=", "");
                            response = gson.toJson(taskManager.getTaskById(parseTaskId(idInString)));
                            sendText(exchange, response, 200);
                            break;
                        }
                    }
                    if (pathParts[2].equals("subtask") && query == null) {
                        response = gson.toJson(taskManager.getSubtasks());
                        sendText(exchange, response, 200);
                        break;
                    }
                    if (pathParts[2].equals("subtask") && query.equals("id") && pathParts.length == 3) {
                        idInString = query.replaceFirst("^id=", "");
                        response = gson.toJson(taskManager.getSubtaskById(parseTaskId(idInString)));
                        sendText(exchange, response, 200);
                        break;
                    }
                    if (pathParts[2].equals("subtask") && pathParts[3].equals("epic")) {
                        idInString = query.replaceFirst("^id=", "");
                        response = String.valueOf(taskManager.getEpicSubtasksList(parseTaskId(idInString)));
                        sendText(exchange, response, 200);
                        break;
                    }
                    if (pathParts[2].equals("epic") && query == null) {
                        response = gson.toJson(taskManager.getEpics());
                        sendText(exchange, response, 200);
                        break;
                    }
                    if (pathParts[2].equals("epic") && query.equals("id") && pathParts.length == 3) {
                        idInString = query.replaceFirst("^id=", "");
                        response = gson.toJson(taskManager.getEpicById(parseTaskId(idInString)));
                        sendText(exchange, response, 200);
                        break;
                    }
                    if (pathParts[2].equals("history")) {
                        response = String.valueOf(taskManager.getHistory());
                        sendText(exchange, response, 200);
                        break;
                    }
                case "POST":
                    if (pathParts[2].equals("task")) {
                        Task task = gson.fromJson(body, Task.class);
                        taskManager.addTask(task);
                        sendText(exchange, "Задача успешно добавлена.", 201);
                        break;
                    }
                    if (pathParts[2].equals("epic")) {
                        Epic epic = gson.fromJson(body, Epic.class);
                        taskManager.addEpic(epic);
                        sendText(exchange, "Эпик успешно добавлен.", 201);
                        break;
                    }
                    if (pathParts[2].equals("subtask")) {
                        Subtask subtask = gson.fromJson(body, Subtask.class);
                        taskManager.addSubtask(subtask);
                        sendText(exchange, "Подзадача успешно добавлена.", 201);
                        break;
                    }
                case "DELETE":
                    if (pathParts[2].equals("task") && query == null) {
                        taskManager.taskAllDelete();
                        sendText(exchange, "Задачи успешно удалены.", 202);
                        break;
                    }
                    if (pathParts[2].equals("epic") && query == null) {
                        taskManager.epicAllDelete();
                        sendText(exchange, "Епики успешно удалены.", 202);
                        break;
                    }
                    if (pathParts[2].equals("subtask") && query == null) {
                        taskManager.subtaskAllDelete();
                        sendText(exchange, "Подзадачи успешно удалены.", 202);
                        break;
                    }
                    if (pathParts[2].equals("task") && query.equals("id")) {
                        idInString = query.replaceFirst("^id=", "");
                        taskManager.deleteTaskInIds(Integer.parseInt(idInString));
                        sendText(exchange, "Задача с ID " + idInString + " успешно удалена.", 202);
                        break;
                    }
                    if (pathParts[2].equals("epic") && query.equals("id")) {
                        idInString = query.replaceFirst("^id=", "");
                        taskManager.deleteEpicInIds(Integer.parseInt(idInString));
                        sendText(exchange, "Епик с ID " + idInString + " успешно удален.", 202);
                        break;
                    }
                    if (pathParts[2].equals("subtask") && query.equals("id")) {
                        idInString = query.replaceFirst("^id=", "");
                        taskManager.deleteSubTaskInIds(Integer.parseInt(idInString));
                        sendText(exchange, "Подзадача с ID " + idInString + " успешно удалена.", 202);
                        break;
                    }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            sendText(exchange, "По указанному адресу нет ресурса. Проверьте URL-адрес ресурса и повторите запрос.", 404);
        } catch (JsonParseException e) {
            sendText(exchange, "При обработке JSON произошла непредвиденная ошибка. Проверьте параметры и повторите запрос.", 400);
        } catch (IOException e) {
            sendText(exchange, "На стороне сервера произошла непредвиденная ошибка. Проверьте параметры и повторите запрос.", 500);
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

    private void sendText(HttpExchange httpExchange, String text, int code) throws IOException {
        byte[] textInByte = text.getBytes(StandardCharsets.UTF_8);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        httpExchange.sendResponseHeaders(code, textInByte.length);
        httpExchange.getResponseBody().write(textInByte);
    }

    private int parseTaskId(String idInPath) {
        try {
            return Integer.parseInt(idInPath);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}