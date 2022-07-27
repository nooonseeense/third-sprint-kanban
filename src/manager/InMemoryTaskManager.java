package manager;

import tasks.Task;

import java.util.List;

public class InMemoryTaskManager implements HistoryManager {
    // должен хранится лист тасков (10 шт)

    @Override
    public void add(Task task) {
        // переопределяем метод добавления из интерфейса
    }

    @Override
    public List<Task> getTasks() {
        return null;
    }
}
