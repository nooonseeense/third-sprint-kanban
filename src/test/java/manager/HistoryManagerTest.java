package manager;

import constants.Status;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class HistoryManagerTest<T extends HistoryManager> {
    TaskManager taskManager = new InMemoryTaskManager();
    T historyManager;

    public HistoryManagerTest(T historyManager) {
        this.historyManager = historyManager;
    }

    @Test
    public void addTaskAndRemoveInHistoryTest() {
        Task task1 = new Task("Задача", "Описание", Status.NEW);
        Task task2 = new Task("Задача", "Описание", Status.DONE);

        assertEquals(0, taskManager.getHistory().size(), "Список истории не пустой.");

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());

        assertEquals(2, taskManager.getHistory().size(), "Дублирование истории вызовов");
        assertEquals(task2, taskManager.getHistory().get(1));
    }


    @Test
    public void getHistoryTest() {
        Task task1 = new Task("Задача", "Описание", Status.NEW, 60,
                LocalDateTime.of(2022, Month.NOVEMBER, 2, 14, 30));
        Task task2 = new Task("Задача2", "Описание2", Status.NEW, 60,
                LocalDateTime.of(2022, Month.DECEMBER, 3, 15, 40));

        taskManager.addTask(task1);
        taskManager.addTask(task2);

        assertEquals(0, taskManager.getHistory().size(), "Список истории не пустой.");

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());

        assertEquals(2, taskManager.getHistory().size(), "Список истории пустой.");
    }
}