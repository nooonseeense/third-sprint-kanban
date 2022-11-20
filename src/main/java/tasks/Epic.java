package tasks;

import constants.Status;
import constants.TaskType;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subtaskIds = new LinkedList<>();

    public Epic(String name,
                String description
    ) {
        super(name, description, Status.NEW);
    }

    // Конструктор для метода public Task fromString(String value)
    public Epic(int id,
                TaskType taskType,
                String name,
                Status status,
                String description,
                int duration,
                LocalDateTime startTime,
                LocalDateTime endTime
    ) {
        super(id, taskType, name, status, description, duration, startTime, endTime);
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void setSubtaskIds(int id) {
        subtaskIds.add(id);
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.EPIC;
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