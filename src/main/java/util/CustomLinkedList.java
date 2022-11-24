package util;

import java.util.ArrayList;
import java.util.List;

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
    public void removeNode(CustomNode<Task> node) {
        if (node == null) {
            return;
        }

        final CustomNode<Task> prev = node.prev;
        final CustomNode<Task> next = node.next;

        if (prev == null) {
            head = next;
        } else {
            prev.next = next;
            node.prev = null;
        }
        if (next == null) {
            tail = prev;
        } else {
            next.prev = prev;
            node.next = null;
        }
        node.task = null;
    }

    @Override
    public List<Task> getTasks() {
        List<Task> tasksList = new ArrayList<>();
        CustomNode<Task> node = head;

        while (node != null) {
            tasksList.add(node.getTask());
            node = node.next;
        }
        return tasksList;
    }

    public CustomNode<Task> getTail() {
        return tail;
    }
}