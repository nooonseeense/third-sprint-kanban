package tasks;

import constants.Status;
import constants.TaskType;

import java.util.Objects;

public class Task {
    protected String name;
    protected String description;
    protected int id;
    protected Status status;
    protected TaskType taskType;

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(int id,TaskType taskType, String name, Status status, String description) {
        this.id = id;
        this.taskType = taskType;
        this.name = name;
        this.status = status;
        this.description = description;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public TaskType getTaskType() {
        return TaskType.TASK;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name)
                && Objects.equals(description, task.description)
                && status == task.status && taskType == task.taskType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status, taskType);
    }

    @Override
    public String toString() {
        return id + ","
                + getTaskType() + ","
                + name + ","
                + status + ","
                + description  + ","
                + "\n";
    }
}