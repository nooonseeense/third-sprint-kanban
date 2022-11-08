package manager;

import tasks.Task;
import util.CustomLinkedList;
import util.CustomNode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    protected final CustomLinkedList<Task> customList = new CustomLinkedList<>();
    protected final Map<Integer, CustomNode<Task>> tasksHistory = new HashMap<>();

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        remove(task.getId());
        customList.linkLast(task);
        tasksHistory.put(task.getId(), customList.getTail());
    }

    @Override
    public void remove(int id) {
        customList.removeNode(tasksHistory.remove(id));
    }

    @Override
    public List<Task> getHistory() {
        return customList.getTasks();
    }
}