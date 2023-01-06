package http_service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import constants.Status;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest {
    private HttpTaskServer taskServer;
    private TaskManager taskManager;
    private final Gson gson = Managers.getGson();
    HttpClient client = HttpClient.newHttpClient();
    KVServer kvServer;

    @BeforeEach
    void init() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        taskServer = new HttpTaskServer();
        taskServer.start();
        taskManager = taskServer.getTaskManager();
    }

    @AfterEach
    void tearDown() {
        taskServer.stop();
        kvServer.stop();
    }

    @Test
    void getPrioritizedTasksTest() throws IOException, InterruptedException {
        Task task = new Task("Task", "Task description", Status.NEW, 120, LocalDateTime.now());
        taskManager.addTask(task);

        Task task2 = new Task("Task2", "Description2", Status.NEW, 60,
                LocalDateTime.of(2022, Month.NOVEMBER, 2, 14, 30));
        taskManager.addTask(task2);

        URI url = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Type listType = new TypeToken<ArrayList<Task>>() {
        }.getType();
        List<Task> actual = gson.fromJson(response.body(), listType);

        assertEquals(200, response.statusCode());
        assertEquals(task.getId(), actual.get(1).getId(), "Objects in sheet are not sorted");
    }

    @Test
    void getTasksTest() throws IOException, InterruptedException {
        Task task = new Task("Task", "Task description", Status.NEW, 120, LocalDateTime.now());
        taskManager.addTask(task);

        Task task2 = new Task("Task2", "Description2", Status.NEW, 60,
                LocalDateTime.of(2022, Month.NOVEMBER, 2, 14, 30));
        taskManager.addTask(task2);

        URI url = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String result = response.body();

        List<Task> expectedList = new ArrayList<>();
        expectedList.add(task);
        expectedList.add(task2);

        Type listType = new TypeToken<ArrayList<Task>>() {
        }.getType();
        List<Task> actual = gson.fromJson(response.body(), listType);

        assertNotNull(result, "Список обычных задач не возвращается");

        assertEquals(expectedList, actual);
        assertNotNull(actual, "Задачи не возвращаются");
        assertEquals(200, response.statusCode());
        assertEquals(2, actual.size(), "Не верное количество задач");
    }

    @Test
    void getEpicsTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epic description");
        taskManager.addEpic(epic);

        Epic epic2 = new Epic("Epic2", "Epic description2");
        taskManager.addEpic(epic2);

        URI url = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String result = response.body();

        List<Epic> expectedList = new ArrayList<>();
        expectedList.add(epic);
        expectedList.add(epic2);

        Type listType = new TypeToken<ArrayList<Epic>>() {
        }.getType();
        List<Epic> actual = gson.fromJson(response.body(), listType);

        assertNotNull(result, "Список эпиков не возвращается");
        assertEquals(expectedList, actual);
        assertEquals(200, response.statusCode());
        assertEquals(2, actual.size(), "Не верное количество эпиков");
    }

    @Test
    void getSubtasksTest() throws IOException, InterruptedException {
        Epic epic3 = new Epic("Epic3", "Epic description2");
        taskManager.addEpic(epic3);

        Subtask subtask3 = new Subtask("Subtask3", "Subtask description", Status.IN_PROGRESS, 160,
                LocalDateTime.of(2023, Month.DECEMBER, 19, 19, 30), epic3.getId());
        taskManager.addSubtask(subtask3);

        URI url = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String result = response.body();

        List<Task> expectedList = new ArrayList<>();
        expectedList.add(subtask3);

        Type listType = new TypeToken<ArrayList<Subtask>>() {
        }.getType();
        List<Subtask> actual = gson.fromJson(response.body(), listType);

        assertNotNull(result, "Список эпиков не возвращается");
        assertEquals(expectedList, actual);
        assertEquals(200, response.statusCode());
        assertEquals(1, actual.size(), "Не верное количество подзадач");
    }

    @Test
    void getTaskByIdTest() throws IOException, InterruptedException {
        Task task = new Task("Task", "Task description", Status.NEW, 120, LocalDateTime.now());
        taskManager.addTask(task);

        URI url = URI.create("http://localhost:8080/tasks/task/?id=" + task.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ошибка при получении задачи.");
        assertEquals(gson.toJson(task), response.body());
    }

    @Test
    void getEpicByIdTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic3", "Epic description2");
        taskManager.addEpic(epic);

        URI url = URI.create("http://localhost:8080/tasks/epic/?id=0");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ошибка при получении эпика.");
        assertEquals(gson.toJson(epic), response.body());
    }

    @Test
    void getSubtaskByIdTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic3", "Epic description2");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask3", "Subtask description", Status.IN_PROGRESS, 160,
                LocalDateTime.of(2023, Month.DECEMBER, 19, 19, 30), epic.getId());
        taskManager.addSubtask(subtask);

        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=" + subtask.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ошибка при получении задачи.");
        assertEquals(gson.toJson(subtask), response.body());
    }

    @Test
    void getEpicSubtasksIdTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic3", "Epic description2");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask3", "Subtask description", Status.IN_PROGRESS, 160,
                LocalDateTime.of(2023, Month.DECEMBER, 19, 19, 30), epic.getId());
        taskManager.addSubtask(subtask);

        URI url = URI.create("http://localhost:8080/tasks/subtask/epic/?id=" + epic.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Type listType = new TypeToken<ArrayList<Integer>>() {
        }.getType();
        List<Integer> actual = gson.fromJson(response.body(), listType);

        assertEquals(200, response.statusCode());
        assertEquals(subtask.getId(), actual.get(0));
    }

    @Test
    void getHistoryTest() throws IOException, InterruptedException {
        Task task = new Task("Task", "Task description", Status.NEW, 120, LocalDateTime.now());
        taskManager.addTask(task);

        Task task2 = new Task("Task2", "Description2", Status.NEW, 60,
                LocalDateTime.of(2022, Month.NOVEMBER, 2, 14, 30));
        taskManager.addTask(task2);

        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(task2.getId());

        URI url = URI.create("http://localhost:8080/tasks/history/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Type listType = new TypeToken<ArrayList<Task>>() {
        }.getType();
        List<Task> actual = gson.fromJson(response.body(), listType);

        assertEquals(200, response.statusCode());
        assertEquals(task, actual.get(0));
        assertEquals(task2, actual.get(1));
    }

    @Test
    void addSubtaskTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epic description");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Subtask description", Status.NEW, 60,
                LocalDateTime.of(2022, Month.NOVEMBER, 8, 19, 30), epic.getId());

        String taskJson = gson.toJson(subtask);
        URI url = URI.create("http://localhost:8080/tasks/subtask/");

        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
    }

    @Test
    void addEpicTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epic description");
        String taskJson = gson.toJson(epic);
        URI url = URI.create("http://localhost:8080/tasks/epic/");

        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
    }

    @Test
    void addTaskTest() throws IOException, InterruptedException {
        Task task = new Task("Task", "Task description", Status.NEW, 120,
                LocalDateTime.of(2024, Month.DECEMBER, 18, 19, 30));
        String taskJson = gson.toJson(task);
        URI url = URI.create("http://localhost:8080/tasks/task/");

        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
    }

    @Test
    void taskAllDeleteTest() throws IOException, InterruptedException {
        Task task = new Task("Task", "Task description", Status.NEW, 120,
                LocalDateTime.of(2024, Month.DECEMBER, 18, 19, 30));
        taskManager.addTask(task);

        Task task2 = new Task("Task2", "Description2", Status.NEW, 60,
                LocalDateTime.of(2022, Month.NOVEMBER, 2, 14, 30));
        taskManager.addTask(task2);

        assertEquals(2, taskManager.getTasks().size(), "The task has not been added");

        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(0, taskManager.getTasks().size(), "List is not empty");
        assertEquals(202, response.statusCode());
    }

    @Test
    void epicAllDeleteTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epic description");
        taskManager.addEpic(epic);

        assertEquals(1, taskManager.getEpics().size(), "The epic has not been added");

        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(0, taskManager.getEpics().size(), "List is not empty");
        assertEquals(202, response.statusCode());
    }

    @Test
    void subtaskAllDeleteTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epic description");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Subtask description", Status.NEW, 60,
                LocalDateTime.of(2022, Month.NOVEMBER, 8, 19, 30), epic.getId());
        taskManager.addSubtask(subtask);

        assertEquals(1, taskManager.getSubtasks().size(), "The subtask has not been added");

        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(0, taskManager.getSubtasks().size(), "List is not empty");
        assertEquals(202, response.statusCode());
    }

    @Test
    void taskDeleteByIdTest() throws IOException, InterruptedException {
        Task task = new Task("TASK", "TASK_DESCRIPTION", Status.NEW);
        taskManager.addTask(task);
        taskManager.getTaskById(task.getId());

        URI url = URI.create("http://localhost:8080/tasks/task/?id=" + task.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(0, taskManager.getTasks().size(), "Task has not been deleted");
        assertEquals(0, taskManager.getHistory().size(), "Task has not been deleted in history");

        assertEquals(202, response.statusCode());
    }

    @Test
    void epicDeleteByIdTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epic description");
        taskManager.addEpic(epic);
        taskManager.getEpicById(epic.getId());

        URI url = URI.create("http://localhost:8080/tasks/epic/?id=" + epic.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(0, taskManager.getEpics().size(), "Task has not been deleted");
        assertEquals(0, taskManager.getHistory().size(), "Task has not been deleted in history");

        assertEquals(202, response.statusCode());
    }

    @Test
    void subtaskDeleteByIdTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epic description");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Subtask description", Status.NEW, 60,
                LocalDateTime.of(2022, Month.NOVEMBER, 8, 19, 30), epic.getId());
        taskManager.addSubtask(subtask);

        taskManager.getSubtaskById(subtask.getId());

        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=" + subtask.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(0, taskManager.getSubtasks().size(), "Task has not been deleted");
        assertEquals(0, taskManager.getHistory().size(), "Task has not been deleted in history");

        assertEquals(202, response.statusCode());
    }

    @Test
    void updateTaskTest() throws IOException, InterruptedException {
        Task task = new Task("TASK", "TASK_DESCRIPTION", Status.NEW, 60,
                LocalDateTime.of(2023, Month.DECEMBER, 2, 14, 30));
        taskManager.addTask(task);

        task = new Task("TASK", "NEW_TASK_DESCRIPTION", Status.NEW, 60,
                LocalDateTime.of(2024, Month.NOVEMBER, 2, 14, 30));

        String taskJson = gson.toJson(task);

        URI url = URI.create("http://localhost:8080/tasks/task/?id=" + task.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


        Task newTask = taskManager.getTaskById(task.getId());

        assertEquals(201, response.statusCode());
        assertEquals(newTask.getStartTime(), task.getStartTime(), "Задача не была обновлена");
    }

    @Test
    void updateEpicTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epic description");
        taskManager.addTask(epic);

        Subtask subtask = new Subtask("Subtask", "Subtask description", Status.NEW, 60,
                LocalDateTime.of(2022, Month.NOVEMBER, 8, 19, 30), epic.getId());
        taskManager.addSubtask(subtask);

        epic = new Epic("Epic", "NEW_Epic description");

        Subtask subtask2 = new Subtask("Subtask", "Subtask description", Status.NEW, 60,
                LocalDateTime.of(2023, Month.JUNE, 7, 18, 30), epic.getId());
        taskManager.addSubtask(subtask2);

        String taskJson = gson.toJson(epic);

        URI url = URI.create("http://localhost:8080/tasks/epic/?id=" + epic.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Task newEpic = taskManager.getEpicById(epic.getId());

        assertEquals(201, response.statusCode());
        assertEquals(newEpic.getStartTime(), epic.getStartTime(), "Эпик не был обновлен");
    }

    @Test
    void updateSubtaskTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epic description");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask", "Subtask description", Status.NEW, 60,
                LocalDateTime.of(2022, Month.NOVEMBER, 8, 19, 30), epic.getId());
        taskManager.addSubtask(subtask);

        subtask = new Subtask("Subtask", "NEW_Subtask description", Status.NEW, 180,
                LocalDateTime.of(2022, Month.DECEMBER, 8, 19, 30), epic.getId());

        String taskJson = gson.toJson(subtask);

        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Task newSubtask = taskManager.getSubtaskById(subtask.getId());

        assertEquals(201, response.statusCode());
        assertEquals(newSubtask.getDuration(), subtask.getDuration(), "Подзадача не была обновлена");
    }
}