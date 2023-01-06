package http_service;

import com.google.gson.*;

import com.sun.net.httpserver.HttpExchange;

import com.sun.net.httpserver.HttpServer;

import constants.Status;
import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.Month;

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
        HttpTaskServer server = new HttpTaskServer();
        server.start();
        TaskManager taskManager = server.getTaskManager();

        Task task1 = new Task("Задача", "Описание", Status.NEW, 60,
                LocalDateTime.of(2022, Month.NOVEMBER, 2, 14, 30));
        taskManager.addTask(task1);
        taskManager.getTaskById(task1.getId());

        Epic epic1 = new Epic("Епик", "Описание");
        taskManager.addEpic(epic1);
        taskManager.getEpicById(epic1.getId());

        Subtask subtask1 = new Subtask("Подзадача", "Описание", Status.IN_PROGRESS, epic1.getId());
        taskManager.addSubtask(subtask1);
        taskManager.getSubtaskById(subtask1.getId());;
    }

    public HttpTaskServer() throws IOException {
        taskManager = Managers.getDefaultTask();
        gson = Managers.getGson();
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/" + TASKS_REQUEST, this::handler);
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    private void handler(HttpExchange h) throws IOException {
        String path = h.getRequestURI().getPath();
        String query = h.getRequestURI().getQuery();
        String method = h.getRequestMethod();
        String body = readText(h);
        String[] pathParts = path.split("/");
        String response;
        int taskId = 0;

        if (query != null) {
            taskId = parsePathId(query.replaceFirst("^id=", ""));

            if (taskId == -1) {
                System.out.println("<SYSTEM>: Получен некорректный id = " + taskId);
                h.sendResponseHeaders(405, 0);
            }
        }

        try {
            switch (method) {
                case "GET":
                    if (pathParts[1].equals(TASKS_REQUEST) && pathParts.length == 2) {
                        if (h.getRequestBody() != null) {
                            response = gson.toJson(taskManager.getPrioritizedTasks());
                            sendText(h, response);
                        }
                        return;
                    }
                    if (pathParts[1].equals(TASKS_REQUEST) && pathParts[2].equals(TASK_REQUEST) && query == null) {
                        response = gson.toJson(taskManager.getTasks());
                        sendText(h, response);
                        return;
                    }
                    if (pathParts[2].equals(TASK_REQUEST)) {
                        response = gson.toJson(taskManager.getTaskById(taskId));
                        sendText(h, response);
                        return;
                    }
                    if (pathParts.length > 3) {
                        if (pathParts[3].equals(EPIC_REQUEST) && query != null) {
                            response = gson.toJson(taskManager.getEpicSubtasksList(taskId));
                            sendText(h, response);
                            return;
                        }
                    }
                    if (pathParts[2].equals(SUBTASK_REQUEST) && query == null) {
                        response = gson.toJson(taskManager.getSubtasks());
                        sendText(h, response);
                        return;
                    }

                    if (pathParts[2].equals(SUBTASK_REQUEST)) {
                        response = gson.toJson(taskManager.getSubtaskById(taskId));
                        sendText(h, response);
                        return;
                    }
                    if (pathParts[2].equals(EPIC_REQUEST) && query == null) {
                        response = gson.toJson(taskManager.getEpics());
                        sendText(h, response);
                        return;
                    }
                    if (pathParts[2].equals(EPIC_REQUEST)) {
                        response = gson.toJson(taskManager.getEpicById(taskId));
                        sendText(h, response);
                        return;
                    }
                    if (pathParts[2].equals(HISTORY_REQUEST)) {
                        response = gson.toJson(taskManager.getHistory());
                        sendText(h, response);
                        return;
                    }
                    break;
                case "POST":
                    if (pathParts[2].equals(TASK_REQUEST)) {
                        Task task = gson.fromJson(body, Task.class);
                        if (taskId >= 0 && query != null) {
                            taskManager.updateTask(task);
                            System.out.println("<SYSTEM>: Задача обновлена");
                        } else {
                            taskManager.addTask(task);
                            System.out.println("<SYSTEM>: Задача создана");
                        }
                        h.sendResponseHeaders(201, 0);
                        return;
                    }
                    if (pathParts[2].equals(EPIC_REQUEST)) {
                        Epic epic = gson.fromJson(body, Epic.class);
                        if (taskId >= 0 && query != null) {
                            taskManager.updateEpic(epic);
                            System.out.println("<SYSTEM>: Эпик обновлен");
                        } else {
                            taskManager.addEpic(epic);
                            System.out.println("<SYSTEM>: Эпик создан");
                        }
                        h.sendResponseHeaders(201, 0);
                        return;
                    }
                    if (pathParts[2].equals(SUBTASK_REQUEST)) {
                        Subtask subtask = gson.fromJson(body, Subtask.class);
                        if (taskId >= 0 && query != null) {
                            taskManager.updateSubtasks(subtask);
                            System.out.println("<SYSTEM>: Подзадача обновлена");
                        } else {
                            taskManager.addSubtask(subtask);
                            System.out.println("<SYSTEM>: Подзадача создана");
                        }
                        h.sendResponseHeaders(201, 0);
                        return;
                    }
                    break;
                case "DELETE":
                    if (pathParts[2].equals(TASK_REQUEST) && query == null) {
                        taskManager.taskAllDelete();
                        System.out.println("<SYSTEM>: Список задач очищен");
                        h.sendResponseHeaders(202, 0);
                        return;
                    }
                    if (pathParts[2].equals(EPIC_REQUEST) && query == null) {
                        taskManager.epicAllDelete();
                        System.out.println("<SYSTEM>: Список эпиков очищен");
                        h.sendResponseHeaders(202, 0);
                        return;
                    }
                    if (pathParts[2].equals(SUBTASK_REQUEST) && query == null) {
                        taskManager.subtaskAllDelete();
                        System.out.println("<SYSTEM>: Список подзадач очищен");
                        h.sendResponseHeaders(202, 0);
                        return;
                    }
                    if (pathParts[2].equals(TASK_REQUEST)) {
                        taskManager.deleteTaskInIds(taskId);
                        System.out.println("<SYSTEM>: Задача удалена, id = " + taskId);
                        h.sendResponseHeaders(202, 0);
                        return;
                    }
                    if (pathParts[2].equals(EPIC_REQUEST)) {
                        taskManager.deleteEpicInIds(taskId);
                        System.out.println("<SYSTEM>: Эпик удален, id = " + taskId);
                        h.sendResponseHeaders(202, 0);
                        return;
                    }
                    if (pathParts[2].equals(SUBTASK_REQUEST)) {
                        taskManager.deleteSubTaskInIds(taskId);
                        System.out.println("<SYSTEM>: Подзадача удалена, id = " + taskId);
                        h.sendResponseHeaders(202, 0);
                        return;
                    }
                    break;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("<SYSTEM>: По указанному адресу нет ресурса. Проверьте URL-адрес ресурса и повторите запрос.");
            h.sendResponseHeaders(404, 0);
        } catch (JsonParseException e) {
            System.out.println("<SYSTEM>: При обработке JSON произошла непредвиденная ошибка. Проверьте параметры и повторите запрос.");
            h.sendResponseHeaders(400, 0);
        } catch (IOException e) {
            System.out.println("<SYSTEM>: На стороне сервера произошла непредвиденная ошибка. Проверьте параметры и повторите запрос.");
            h.sendResponseHeaders(500, 0);
        } finally {
            h.close();
        }
    }

    public void start() {
        System.out.println("<SYSTEM>: Started TaskServer in PORT: " + PORT);
        System.out.println("<SYSTEM>: http://localhost:" + PORT + "/tasks/");
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("<SYSTEM>: Server stopped in PORT: " + PORT);
    }

    private String readText(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes(), UTF_8);
    }

    private int parsePathId(String path) {
        try {
            return Integer.parseInt(path);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void sendText(HttpExchange httpExchange, String text) throws IOException {
        byte[] textInByte = text.getBytes(StandardCharsets.UTF_8);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        httpExchange.sendResponseHeaders(200, textInByte.length);
        httpExchange.getResponseBody().write(textInByte);
    }
}