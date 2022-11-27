package manager;

import constants.Status;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class HistoryManagerTest<T extends HistoryManager> {
    T historyManager;

    public HistoryManagerTest(T historyManager) {
        this.historyManager = historyManager;
    }

    @Test
    public void addTaskAndRemoveInHistoryTest() {
        Task task1 = new Task(
                0,
                "Задача",
                Status.NEW,
                "Описание",
                60,
                LocalDateTime.of(2022, Month.NOVEMBER, 1, 15, 0),
                LocalDateTime.of(2022, Month.NOVEMBER, 1, 16, 0)
        );
        Task task2 = new Task(
                1,
                "Задача2",
                Status.IN_PROGRESS,
                "Описание2",
                65,
                LocalDateTime.of(2022, Month.NOVEMBER, 1, 17, 0),
                LocalDateTime.of(2022, Month.NOVEMBER, 1, 18, 5)
        );

        assertEquals(0, historyManager.getHistory().size(), "Список истории не пустой.");

        historyManager.add(task1);
        historyManager.add(task2);

        assertEquals(2, historyManager.getHistory().size(), "Дублирование истории вызовов");
        assertEquals(task2, historyManager.getHistory().get(1));

        Task task3 = new Task(
                0,
                "Задача",
                Status.NEW,
                "Описание",
                60,
                LocalDateTime.of(2022, Month.NOVEMBER, 1, 15, 0),
                LocalDateTime.of(2022, Month.NOVEMBER, 1, 16, 0)
        );
        historyManager.add(task3);
        historyManager.remove(task2.getId());

        assertEquals(1, historyManager.getHistory().size());
    }
}