package tasks;

import constants.Status;
import constants.TaskType;
import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtaskIds = new ArrayList<>();

    public Epic(String name,
                String description
    ) {
        super(name, description, Status.NEW); // сюда добавим три метода по расчету duration startTime endTime
    }

    public Epic(int id,
                TaskType taskType,
                String name,
                Status status,
                String description
    ) {
       super(id, taskType, name, status, description); // сюда добавим три метода по расчету duration startTime endTime
    }

    public ArrayList<Integer> getSubtaskIds () {
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
                + description  + ","
                + duration + ","
                + startTime + ","
                + endTime + "\n";
    }
}