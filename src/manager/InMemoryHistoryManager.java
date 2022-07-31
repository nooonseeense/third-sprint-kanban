package manager;

import tasks.Task;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> tasksHistory = new LinkedList<>();
    private int counterHistory = 0;

    @Override
    public void add(Task task) {
        int MAX_LIST_SIZE = 10;

        tasksHistory.add(task);
        counterHistory++;

        if (counterHistory == MAX_LIST_SIZE) {
            tasksHistory.remove(0);
            counterHistory--;
        }
    }

    @Override
    public List<Task> getHistory() {
        return tasksHistory;
    }

    @Override
    public String toString() {
        return "InMemoryHistoryManager{" +
                "tasksHistory=" + tasksHistory +
                '}';
    }
}
