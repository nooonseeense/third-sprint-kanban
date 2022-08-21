package manager;

import tasks.Task;
import util.CustomLinkedList;
import util.CustomNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final CustomLinkedList<Task> customList = new CustomLinkedList<>();
    private final Map<Integer, CustomNode<Task>> tasksHistory = new HashMap<>();

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
        customList.removeNode(id, tasksHistory);
    }

    @Override
    public List<Task> getHistory() {
        List<Task> tasksList = new ArrayList<>();

        for (CustomNode<Task> task : tasksHistory.values()) {
            tasksList.add(task.getTask());
        }

        return tasksList;
    }
}
