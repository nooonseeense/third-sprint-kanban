package manager;

import tasks.Task;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> tasksHistory = new LinkedList<>();
    private static final int MAX_LIST_SIZE = 10;

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }

        tasksHistory.add(task);
        if (tasksHistory.size() == MAX_LIST_SIZE) {
            tasksHistory.remove(0);
        }
    }

    @Override
    public List<Task> getHistory() {
        return List.copyOf(tasksHistory);
    }
}
