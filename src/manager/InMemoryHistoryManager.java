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
    private final Map<Integer, CustomNode<Task>> nodesMap = new HashMap<>();

    @Override
    public void add(Task task) {
        remove(task.getId());
        customList.linkLast(task);
        nodesMap.put(task.getId(), customList.getTail());
    }

    @Override
    public void remove(int id) {
        customList.removeNode(id, getNodesMap());
    }

    @Override
    public List<Task> getHistory() {
        List<Task> tasksList = new ArrayList<>();

        for (CustomNode<Task> task : nodesMap.values()) {
            tasksList.add(task.getTask());
        }

        return List.copyOf(tasksList);
    }

    public Map<Integer, CustomNode<Task>> getNodesMap() {
        return nodesMap;
    }
}
