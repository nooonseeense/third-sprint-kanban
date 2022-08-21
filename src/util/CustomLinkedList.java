package util;

import java.util.Map;

public class CustomLinkedList<Task> implements CustomList<Task> {
    private CustomNode<Task> head;
    private CustomNode<Task> tail;

    @Override
    public void linkLast(Task element) {
        final CustomNode<Task> oldTail = tail;
        final CustomNode<Task> newNode = new CustomNode<>(oldTail, element, null);
        tail = newNode;

        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
    }

    @Override
    public void removeNode(int id, Map<Integer, CustomNode<Task>> nodesMap) {
        nodesMap.remove(id);
    }

    @Override
    public void getTasks() {

    }

    public CustomNode<Task> getTail() {
        return tail;
    }
}
