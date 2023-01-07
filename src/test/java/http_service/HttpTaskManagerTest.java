package http_service;

import constants.Status;
import manager.FileBackedTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskManagerTest extends FileBackedTaskManager {
    private KVServer kVServer;
    private HttpTaskServer taskServer;
    private TaskManager taskManager;

    @BeforeEach
    void init() throws IOException {
        kVServer = new KVServer();
        kVServer.start();
        taskServer = new HttpTaskServer();
        taskServer.start();
        taskManager = taskServer.getTaskManager();
    }

    @Test
    void loadTest() throws IOException {
        Task task1 = new Task("Задача", "Описание", Status.NEW,
                60, LocalDateTime.of(2022, Month.NOVEMBER, 1, 15, 0));
        taskManager.addTask(task1);
        taskManager.getTaskById(task1.getId());

        Epic epic1 = new Epic("Епик", "Описание");
        taskManager.addEpic(epic1);
        taskManager.getEpicById(epic1.getId());

        Subtask subtask1 = new Subtask("Подзадача", "Описание", Status.IN_PROGRESS, epic1.getId());
        taskManager.addSubtask(subtask1);
        taskManager.getSubtaskById(subtask1.getId());

        List<Task> oldAllTasks = taskManager.getAllTasks();
        List<Task> oldTasks = taskManager.getTasks();
        List<Epic> oldEpics = taskManager.getEpics();
        List<Subtask> oldSubtasks = taskManager.getSubtasks();
        List<Task> oldTasksHistory = taskManager.getHistory();
        Integer oldGenerator = taskManager.getGenerator();

        taskServer.stop();
        new HttpTaskServer().start();

        assertEquals(taskManager.getTasks().size(), oldTasks.size(),
                "Task не был добавлен");
        assertEquals(taskManager.getEpics().size(), oldEpics.size(),
                "Epic не был добавлен");
        assertEquals(taskManager.getSubtasks().size(), oldSubtasks.size(),
                "Subtask не был добавлен");
        assertEquals(taskManager.getHistory().size(), oldTasksHistory.size(),
                "Неверное кол-во элементов в истории");
        assertEquals(taskManager.getAllTasks(), oldAllTasks);
        assertEquals(taskManager.getTasks(), oldTasks,
                "Задачи не совпадают");
        assertEquals(taskManager.getEpics(), oldEpics,
                "Епики не совпадают");
        assertEquals(taskManager.getSubtasks(), oldSubtasks,
                "Сабтаски не совпадают");
        assertEquals(taskManager.getHistory(), oldTasksHistory,
                "История не совпадает");
        assertEquals(taskManager.getGenerator(), oldGenerator,
                "ИД последней добавленной задачи не совпадает");
    }

    @AfterEach
    void stopServer() {
        taskServer.stop();
        kVServer.stop();
    }
}
