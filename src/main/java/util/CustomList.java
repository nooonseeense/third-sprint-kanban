package util;

import java.util.List;
import java.util.Map;

public interface CustomList<Task> {

    void linkLast(Task task);

    void removeNode(CustomNode<Task> nodes);

    List<Task> getTasks();
}