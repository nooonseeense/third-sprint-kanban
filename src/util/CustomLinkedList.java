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
        if (node.prev == null) {
            head = node.next;
        }
        if (node.next == null) {
            tail = node.prev;
        }
        if (node.prev != null && node.next != null) {
            CustomNode<Task> left = node.prev;
            CustomNode<Task> right = node.next;

            left.next = right;
            right.prev = left;

            node.next = null;
            node.prev = null;
        }
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
