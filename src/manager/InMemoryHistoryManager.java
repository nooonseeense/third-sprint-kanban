package manager;

import tasks.Task;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> tasksHistory = new LinkedList<>();
    private final static int MAX_LIST_SIZE = 10;

    @Override
    public void add(Task task) {
        tasksHistory.add(task);

        if (tasksHistory.size() == MAX_LIST_SIZE) {
            tasksHistory.remove(0);
        }
    }

    @Override
    public List<Task> getHistory() {
        return List.copyOf(tasksHistory);
    }

    @Override
    public String toString() {
        return "InMemoryHistoryManager{" +
                "tasksHistory=" + tasksHistory +
                '}';
    }
}
