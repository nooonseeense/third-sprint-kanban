package manager;

import constants.Status;
import exceptions.ManagerSaveException;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.*;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    FileBackedTaskManager loadFromFile;
    InMemoryTaskManager taskManager = new InMemoryTaskManager();

    public FileBackedTaskManagerTest() {
        super(new FileBackedTaskManager(new File("src/main/java/data/data.csv")));
        loadFromFile = new FileBackedTaskManager(new File("src/main/java/data/data.csv"));
    }

    @Test
    public void saveTest() {
        String file = "src/main/java/data/data.csv";
        Task task1 = new Task("Задача", "Описание", Status.NEW,
                60, LocalDateTime.of(2022, Month.NOVEMBER, 1, 15, 0));
        loadFromFile.addTask(task1);
        loadFromFile.getTaskById(task1.getId());

        Epic epic1 = new Epic("Епик", "Описание");
        loadFromFile.addEpic(epic1);
        loadFromFile.getEpicById(epic1.getId());

        Subtask subtask1 = new Subtask("Подзадача", "Описание", Status.IN_PROGRESS, epic1.getId());
        loadFromFile.addSubtask(subtask1);
        loadFromFile.getSubtaskById(subtask1.getId());

        String taskString = "0,TASK,Задача,NEW,Описание,60,2022-11-01T15:00,2022-11-01T16:00";
        String epicString = "1,EPIC,Епик,IN_PROGRESS,Описание,0,null,null";
        String subtaskString = "2,SUBTASK,Подзадача,IN_PROGRESS,Описание,0,null,null,1";


        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();

                if (line.contains(FileBackedTaskManager.HEADER) || line.equals("")) {
                    continue;
                }

                if (line.contains("TASK") || line.contains("EPIC") || line.contains("SUBTASK")) {
                    Task receivedTask = loadFromFile.fromString(line);

                    if (receivedTask.getId() > loadFromFile.getGenerator()) {
                        switch (receivedTask.getTaskType()) {
                            case TASK:
                                assertEquals(taskString, line, "Задача не записана");
                                break;
                            case SUBTASK:
                                assertEquals(subtaskString, line, "Подзадача не записана");
                                break;
                            case EPIC:
                                assertEquals(epicString, line,"Епик не записан");
                                break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
    }

    @Test
    public void loadFromFileTest() {
        Task task1 = new Task("Задача", "Описание", Status.NEW, 60,
                LocalDateTime.of(2022, Month.NOVEMBER, 2, 14, 30));
        taskManager.addTask(task1);
        loadFromFile.addTask(task1);
        taskManager.getTaskById(task1.getId());
        loadFromFile.getTaskById(task1.getId());

        Epic epic1 = new Epic("Епик", "Описание");
        taskManager.addEpic(epic1);
        loadFromFile.addEpic(epic1);
        taskManager.getEpicById(epic1.getId());
        loadFromFile.getEpicById(epic1.getId());

        Subtask subtask1 = new Subtask("Подзадача", "Описание", Status.IN_PROGRESS, epic1.getId());
        taskManager.addSubtask(subtask1);
        loadFromFile.addSubtask(subtask1);
        taskManager.getSubtaskById(subtask1.getId());
        loadFromFile.getSubtaskById(subtask1.getId());

        assertEquals(1, taskManager.getTasks().size(), "Task не был добавлен");
        assertEquals(1, taskManager.getEpics().size(), "Epic не был добавлен");
        assertEquals(1, taskManager.getSubtask().size(), "Subtask не был добавлен");
        assertEquals(3, taskManager.getHistory().size(), "Неверное кол-во элементов в истории");
        assertEquals(taskManager.getAllTasks(), loadFromFile.getAllTasks(),
                "Список задач после выгрузки не совпадает");
        assertEquals(taskManager.getTasks().get(task1.getId()), loadFromFile.getTasks().get(task1.getId()),
                "Задача не совпадает");
        assertEquals(taskManager.getEpics().get(0), loadFromFile.getEpics().get(0),
                "Епик не совпадает");
        assertEquals(taskManager.getSubtask().get(0), loadFromFile.getSubtask().get(0),
                "Сабтаск не совпадает");
        assertEquals(3, taskManager.getGenerator(), "ИД последней добавленной задачи не совпадает");
        assertEquals(taskManager.sortedListTasksAndSubtasks, loadFromFile.sortedListTasksAndSubtasks,
                "Отсортированный список не совпадает");
        assertEquals(taskManager.getHistory(), loadFromFile.getHistory(), "История не совпадает");
    }
}