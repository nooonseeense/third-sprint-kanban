package manager;

import tasks.Task;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> tasksHistory = new LinkedList<>();

    @Override
    public void add(Task task) {
        // some logic
    }

    @Override
    public List<Task> getHistory() {
        // some logic
        return tasksHistory;
    }
}
