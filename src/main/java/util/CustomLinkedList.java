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

        final CustomNode<Task> prev = node.prev; // ССЫЛКА на предыдущую ноду
        final CustomNode<Task> next = node.next; // ССЫЛКА на следующую ноду

        if (prev == null) { // ЕСЛИ предыдущий узел == null, ТО головой списка становится СЛЕДУЮЩИЙ узел
            head = next;
        } else { // ЕСЛИ узел находится в центре списка, ТО:
            prev.next = next; // ТО поле NEXT у предыдущей ноды, начинает ссылаться на поле NEXT удаляемой
            node.prev = null; // поле PREV у удаляемой ноды зануляем, т.к мы изменили ссылки и сохранили связь
        }
        if (next == null) { // ЕСЛИ следующий узел == null, то хвостом списка становится ПРЕДЫДУЩИЙ узел
            tail = prev;
        } else { // ЕСЛИ узел находится в центре списка, ТО:
            next.prev = prev; // ТО поле PREV у следующей ноды, начинает ссылаться на поле PREV удаляемой
            node.next = null; // поле NEXT у удаляемой ноды зануляем, т.к мы изменили ссылки и сохранили связь
        }
        node.task = null; // Зануляем значение узла
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