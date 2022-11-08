package tasks;

import constants.Status;
import constants.TaskType;

import java.time.LocalDateTime;

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

    public Subtask(int id,
                   TaskType taskType,
                   String name,
                   Status status,
                   String description,
                   int duration,
                   LocalDateTime startTime,
                   int epicId
    ) {
        super(id, taskType, name, status, description, duration, startTime);
        this.epicId = epicId;
        endTime = getEndTime();
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() { // переопределить
        return id + ","
                + getTaskType() + ","
                + name + ","
                + status + ","
                + description  + ","
                + duration + ","
                + startTime + ","
                + endTime + ","
                + epicId + ","
                + "\n";
    }
}