package service;
import epics.Epic;
import epics.Subtask;
import epics.Task;

import java.util.ArrayList;
import java.util.List;

public class PrintConsole {
    static final String REPEATED = new String(new char[120]).replace("\0", "=");

    public void printTasks(List<Task> tasks) {
        System.out.println(REPEATED + "\nТаски:");
        for (Task values : tasks) {
            System.out.println(values);
        }
    }

    public void printEpics(List<Epic> epics) {
        System.out.println(REPEATED + "\nЭпики:");
        for (Epic epic : epics) {
            System.out.println("У эпика " + epic + ", подзадача: " + epic.getSubtaskIds());
        }
        System.out.println(REPEATED);
    }

    public void printSubTasksInEpic(Epic epic, List<Subtask> subtask) {
        ArrayList<Subtask> values = new ArrayList<>();

        for (Subtask value : subtask) {
            if (value.getEpicId() == epic.getId()) {
                values.add(value);
            }
        }
        System.out.println("У эпика " + epic + ", подзадачи: " + values);
    }
}