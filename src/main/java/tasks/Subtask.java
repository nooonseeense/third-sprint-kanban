package tasks;

import constants.Status;
import constants.TaskType;

import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String name,
                   String description,
                   Status status,
                   int duration,
                   LocalDateTime startTime,
                   int epicId
    ) {
        super(name, description, status, duration, startTime);
        this.epicId = epicId;
        endTime = getEndTime();
    }
    // Конструктор для метода public Task fromString(String value)
    public Subtask(int id,
                   String name,
                   Status status,
                   String description,
                   int duration,
                   LocalDateTime startTime,
                   LocalDateTime endTime,
                   int epicId
    ) {
        super(id, name, status, description, duration, startTime, endTime);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, Status status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.SUBTASK;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
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
                + endTime + ","
                + epicId + "\n";
    }
}