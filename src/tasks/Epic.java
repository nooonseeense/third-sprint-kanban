package tasks;

import constants.Status;
import java.util.ArrayList;

public class Epic extends Task { // Хранятся просто номера
    protected ArrayList<Integer> subtaskIds = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
    }

    public ArrayList<Integer> getSubtaskIds () {
        return subtaskIds;
    }
}
