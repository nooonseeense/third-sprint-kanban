package http_service;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
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
    private static final String TASK_REQUEST = "task";
    private static final String TASKS_REQUEST = "tasks";
    private static final String EPIC_REQUEST = "epic";
    private static final String SUBTASK_REQUEST = "subtask";
    private static final String HISTORY_REQUEST = "history";

    public static void main(String[] args) throws IOException {
        new HttpTaskServer().start();
    }

    public HttpTaskServer() throws IOException {
        taskManager = Managers.getDefaultTask();
        gson = Managers.getGson();
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext(TASKS_REQUEST, this::handler);
    }

    private void handler(HttpExchange h) throws IOException {
        URI uri = h.getRequestURI();
        String path = uri.getPath();
        String query = uri.getQuery();
        String method = h.getRequestMethod();
        String body = readText(h);
        String[] pathParts = path.split("/");
        String response;
        int taskId = 0;

        if (query != null) {
            taskId = Integer.parseInt(query.replaceFirst("^id=", ""));
        }

        try {
            switch (method) {
                case "GET":
                    if (pathParts[1].equals(TASKS_REQUEST) && pathParts.length == 2) {
                        if (h.getRequestBody() != null) {
                            response = String.valueOf(taskManager.getPrioritizedTasks());
                            sendText(h, response, 200);
                        }
                        break;
                    }
                    if (pathParts[1].equals(TASKS_REQUEST) && pathParts[2].equals(TASK_REQUEST) && query == null) {
                        response = gson.toJson(taskManager.getTasks());
                        sendText(h, response, 200);
                        break;
                    }
                    if (pathParts[2].equals(TASK_REQUEST)) {
                        response = gson.toJson(taskManager.getTaskById(taskId));
                        sendText(h, response, 200);
                        break;
                    }
                    if (pathParts[2].equals(SUBTASK_REQUEST) && pathParts[3].equals(EPIC_REQUEST)) {
                        response = String.valueOf(taskManager.getEpicSubtasksList(taskId));
                        sendText(h, response, 200);
                        break;
                    }
                    if (pathParts[2].equals(SUBTASK_REQUEST) && query == null) {
                        response = gson.toJson(taskManager.getSubtasks());
                        sendText(h, response, 200);
                        break;
                    }
                    if (pathParts[2].equals(SUBTASK_REQUEST)) {
                        response = gson.toJson(taskManager.getSubtaskById(taskId));
                        sendText(h, response, 200);
                        break;
                    }
                    if (pathParts[2].equals(EPIC_REQUEST) && query == null) {
                        response = gson.toJson(taskManager.getEpics());
                        sendText(h, response, 200);
                        break;
                    }
                    if (pathParts[2].equals(EPIC_REQUEST)) {
                        response = gson.toJson(taskManager.getEpicById(taskId));
                        sendText(h, response, 200);
                        break;
                    }
                    if (pathParts[2].equals(HISTORY_REQUEST)) {
                        response = String.valueOf(taskManager.getHistory());
                        sendText(h, response, 200);
                        break;
                    }
                    break;
                case "POST":
                    if (pathParts[2].equals(TASK_REQUEST)) {
                        Task task = gson.fromJson(body, Task.class);
                        taskManager.addTask(task);
                        sendText(h, "Задача успешно добавлена.", 201);
                        break;
                    }
                    if (pathParts[2].equals(EPIC_REQUEST)) {
                        Epic epic = gson.fromJson(body, Epic.class);
                        taskManager.addEpic(epic);
                        sendText(h, "Эпик успешно добавлен.", 201);
                        break;
                    }
                    if (pathParts[2].equals(SUBTASK_REQUEST)) {
                        Subtask subtask = gson.fromJson(body, Subtask.class);
                        taskManager.addSubtask(subtask);
                        sendText(h, "Подзадача успешно добавлена.", 201);
                        break;
                    }
                    break;
                case "DELETE":
                    if (pathParts[2].equals(TASK_REQUEST) && query == null) {
                        taskManager.taskAllDelete();
                        sendText(h, "Задачи успешно удалены.", 202);
                        break;
                    }
                    if (pathParts[2].equals(EPIC_REQUEST) && query == null) {
                        taskManager.epicAllDelete();
                        sendText(h, "Епики успешно удалены.", 202);
                        break;
                    }
                    if (pathParts[2].equals(SUBTASK_REQUEST) && query == null) {
                        taskManager.subtaskAllDelete();
                        sendText(h, "Подзадачи успешно удалены.", 202);
                        break;
                    }
                    if (pathParts[2].equals(TASK_REQUEST)) {
                        taskManager.deleteTaskInIds(taskId);
                        sendText(h, "Задача с ID " + taskId + " успешно удалена.", 202);
                        break;
                    }
                    if (pathParts[2].equals(EPIC_REQUEST)) {
                        taskManager.deleteEpicInIds(taskId);
                        sendText(h, "Епик с ID " + taskId + " успешно удален.", 202);
                        break;
                    }
                    if (pathParts[2].equals(SUBTASK_REQUEST)) {
                        taskManager.deleteSubTaskInIds(taskId);
                        sendText(h, "Подзадача с ID " + taskId + " успешно удалена.", 202);
                        break;
                    }
                    break;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            sendText(h, "По указанному адресу нет ресурса. Проверьте URL-адрес ресурса и повторите запрос.", 404);
        } catch (JsonParseException e) {
            sendText(h, "При обработке JSON произошла непредвиденная ошибка. Проверьте параметры и повторите запрос.", 400);
        } catch (IOException e) {
            sendText(h, "На стороне сервера произошла непредвиденная ошибка. Проверьте параметры и повторите запрос.", 500);
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
}