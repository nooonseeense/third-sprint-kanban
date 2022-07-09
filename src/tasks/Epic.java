package tasks;

import constants.Status;
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtaskIds = new ArrayList<>(); // Хранятся просто номера

    public Epic(String name, String description, Status status) {
        super(name, description, status);
    }
// Добавляем id ПОДЗАДАЧ В ЛИСТ subtaskIds
    public void addSubtask(int subtaskId) {
        subtaskIds.add(subtaskId);
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }
}
