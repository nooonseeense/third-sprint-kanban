package http_service;

import com.google.gson.Gson;
import constants.Status;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskServerTest {
    private HttpTaskServer taskServer;
    private TaskManager taskManager;
    private final Gson gson = Managers.getGson();
    HttpClient client = HttpClient.newHttpClient();
    KVServer kvServer;

    private Task task;
    private Epic epic;
    private Subtask subtask;

    @BeforeEach
    void init() throws IOException {
        kvServer = new KVServer();
        kvServer.start();

        taskManager = Managers.getDefaultTask();

        taskServer = new HttpTaskServer();
        taskServer.start();

        task = new Task("Task", "Task description", Status.NEW, 120, LocalDateTime.now());
        taskManager.addTask(task);

        epic = new Epic("Epic", "Epic description");
        taskManager.addEpic(epic);

        subtask = new Subtask("Subtask", "Subtask description", Status.NEW, 60, LocalDateTime.now(), epic.getId());
        taskManager.addSubtask(subtask);
    }

    @AfterEach
    void tearDown() {
        taskServer.stop();
        kvServer.stop();
    }

    @Test
    void getTasks() {


    }

    @Test
    void getTasksById() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task/?id=0");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(task.toString(), response.body());
    }
}