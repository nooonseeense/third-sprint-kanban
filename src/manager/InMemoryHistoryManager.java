package manager;

import tasks.Task;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> nodesMap = new HashMap<>(); // храним порядок вызовов
    private final HandMadeLinkedList<Task> tasksList = new HandMadeLinkedList<>(); //  храним все задачи

    private final List<Task> tasksHistory = new LinkedList<>(); // удаляем после реализации своего листа
    private static final int MAX_LIST_SIZE = 10;

    @Override
    public void add(Task task) {
        // linkLast();
        // removeMode(o);
        
        if (task == null) {
            return;
        }
        if (tasksHistory.size() == MAX_LIST_SIZE) {
            tasksHistory.remove(0);
        }
        tasksHistory.add(task);
    }

    @Override
    public void remove(int id) {
        tasksHistory.removeIf(taskNext -> taskNext.getId() == id);
    }

    @Override
    public List<Task> getHistory() {
        return List.copyOf(tasksHistory);
    }

    @Override
    public String toString() {
        return "InMemoryHistoryManager{" +
                "tasksHistory=" + tasksHistory +
                '}';
    }

    static class HandMadeLinkedList<T> {
        private Node first;
        private Node Last;

        // private void linkLast() {} - добавляет задачу в конец двусвязного списка

        // public List<Task> getTask() {} - должен собирать все задачи в обычный ArrayList

        // public void removeNode(Node o) {} - принимает объект и вырезает его

    }

    static class Node {
        protected Task task;
        protected Node next;
        protected Node prev;

        public Node(Node prev, Task task, Node next) {
            this.task = task;
            this.next = next;
            this.prev = prev;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "task=" + task +
                    ", prev=" + (prev != null ? prev.task : null) +
                    ", next=" + (next != null ? next.task : null) +
                    '}';
        }
    }
}
