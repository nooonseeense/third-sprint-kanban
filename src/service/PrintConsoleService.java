package service;
import tasks.Epic;
import tasks.Task;

import java.util.List;

public class PrintConsoleService {
// 3.1 Получение списка всех задач мапы task
    public void printTasks(List<Task> tasks) {
        System.out.println("Таски:");
        for (Task values : tasks) {
            System.out.println(values);
        }
    }

    public void printEpics(List<Epic> epics) {
        System.out.println("Эпики:");
        for (Epic epic : epics) {
            System.out.println("У эпика " + epic + ", подзадача: " + epic.getSubtaskIds());
        }
    }
}
