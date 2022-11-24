package manager;

import constants.Status;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.*;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    FileBackedTaskManager fileBackedTasksManager =
            FileBackedTaskManager.loadFromFile(new File("src/main/java/data/data.csv"));

    public FileBackedTaskManagerTest() {
        super(new FileBackedTaskManager(new File("src/main/java/data/data.csv")));
    }

    @Test
    public void saveTest() {
        Epic epic1 = new Epic("Епик[1]", "Описание[Епик]");

        assertEquals(0, fileBackedTasksManager.getEpics().size(), "Список эпиков не пустой");

        fileBackedTasksManager.addEpic(epic1);
        assertEquals(epic1, fileBackedTasksManager.getEpicById(epic1.getId()), "Эпик не был добавлен");

        assertEquals(0, fileBackedTasksManager.getHistory().size(), "Список истории не пустой");
    }

    @Test
    public void loadFromFileTest() {
        Task task1 = new Task("Задача", "Описание", Status.NEW, 60,
                LocalDateTime.of(2022, Month.NOVEMBER, 2, 14, 30));
        fileBackedTasksManager.addTask(task1);
        fileBackedTasksManager.getTaskById(task1.getId());

        Epic epic1 = new Epic("Епик[1]", "Описание[Епик]");
        fileBackedTasksManager.addEpic(epic1);
        fileBackedTasksManager.getEpicById(epic1.getId());

        Subtask subtask1 = new Subtask("Подзадача[1]", "Описание[2]", Status.IN_PROGRESS,
                epic1.getId());
        fileBackedTasksManager.addSubtask(subtask1);
        fileBackedTasksManager.getSubtaskById(subtask1.getId());

        assertEquals(1, fileBackedTasksManager.getTasks().size(), "Task не был добавлен");
        assertEquals(1, fileBackedTasksManager.getEpics().size(), "Epic не был добавлен");
        assertEquals(1, fileBackedTasksManager.getSubtask().size(), "Subtask не был добавлен");

        assertEquals(3, fileBackedTasksManager.getHistory().size(),
                "Неверное количество элементов в истории просмотра");
    }
}
