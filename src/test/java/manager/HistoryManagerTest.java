package manager;

import constants.Status;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.time.LocalDateTime;
import java.time.Month;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HistoryManagerTest<T extends HistoryManager> {
    TaskManager taskManager = new InMemoryTaskManager();
    T historyManager;

    public HistoryManagerTest(T historyManager) {
        this.historyManager = historyManager;
    }
    @Test
    void addTest() {
        Task task1 = new Task("Задача", "Описание", Status.NEW, 60,
                LocalDateTime.of(2022, Month.NOVEMBER, 2, 14, 30));
        Task task2 = new Task("Задача2", "Описание2", Status.NEW, 60,
                LocalDateTime.of(2022, Month.DECEMBER, 3, 15, 40));
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        assertEquals(0, historyManager.getHistory().size(), "History list is not empty");

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());




    }

    @Test
    void removeTest() {


    }

    @Test
    void getHistoryTest() {


    }
}