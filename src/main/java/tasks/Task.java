package tasks;


import constants.Status;
import constants.TaskType;

import java.time.LocalDateTime;
import java.util.Objects;

public class Task implements Comparable<Task> {
    protected String name;
    protected String description;
    protected Integer id;
    protected Status status;
    protected int duration;
    protected LocalDateTime startTime;
    protected LocalDateTime endTime;

    /**
     * Базовый конструктор для создания задачи
     */
    public Task(String name,
                String description,
                Status status,
                int duration,
                LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
        endTime = getEndTime();
    }

    /**
     * Базовый конструктор для создания Epic
     */
    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    /**
     * Конструктор для метода public Task fromString(String value)
     */
    public Task(Integer id,
                String name,
                Status status,
                String description,
                int duration,
                LocalDateTime startTime,
                LocalDateTime endTime) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.description = description;
        this.duration = duration;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public int getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getEndTime() {
        if (getStartTime() == null) {
            return null;
        }
        return startTime.plusSeconds(duration * 60L);
    }

    @Override
    public int compareTo(Task o) {
        if (this.startTime == null && o.startTime == null) {
            return Integer.compare(this.getId(), o.getId());
        }
        if (this.startTime == null) {
            return 1;
        }
        if (o.startTime == null) {
            return -1;
        }
        return this.startTime.compareTo(o.startTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id)
                && duration == task.duration
                && Objects.equals(name, task.name)
                && Objects.equals(description, task.description)
                && status == task.status && Objects.equals(startTime, task.startTime)
                && Objects.equals(endTime, task.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status, duration, startTime, endTime);
    }

    @Override
    public String toString() {
        return id + ","
                + getTaskType() + ","
                + name + ","
                + status + ","
                + description + ","
                + duration + ","
                + startTime + ","
                + endTime + "\n";
    }
}