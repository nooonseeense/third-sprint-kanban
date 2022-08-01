package manager;

import tasks.Task;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> tasksHistory = new LinkedList<>();

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        tasksHistory.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return tasksHistory;
    }
}
