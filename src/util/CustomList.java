package util;

import java.util.Map;

public interface CustomList<Task> {

    void linkLast(Task task);

    void removeNode(int id, Map<Integer, CustomNode<Task>> nodesMap);
}

