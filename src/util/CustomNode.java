package util;

public class CustomNode<Task> {
    Task task;
    CustomNode<Task> next;
    CustomNode<Task> prev;

    public CustomNode(CustomNode<Task> prev, Task task, CustomNode<Task> next) {
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

    public Task getTask() {
        return task;
    }
}
