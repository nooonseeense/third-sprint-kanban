package tasks;

import constants.Status;
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtaskIds = new ArrayList<>();

    public Epic(String name, String description, Status status, ArrayList<Integer> subtaskIds) {
        super(name, description, status);
        this.subtaskIds = subtaskIds;
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void addSubtask(int subtaskId) {
        subtaskIds.add(subtaskId);
    }
}
