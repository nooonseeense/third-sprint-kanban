import constants.Status;
import manager.Managers;
import manager.TaskManager;
import service.PrintConsole;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefaultTask();

        System.out.println("\n----------_SPRINT3_--------------\n");

        Task task1 = new Task("Задача[1]", "Описание[1]", Status.NEW);
        Task task2 = new Task("Задача[2]", "Описание[2]", Status.IN_PROGRESS);

        taskManager.addTask(task1);
        taskManager.addTask(task2);

        Epic epic1 = new Epic("Епик[1]", "Описание[1]");
        Epic epic2 = new Epic("Епик[2]", "Описание[2]");

        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        Subtask subtask1 = new Subtask("Подзадача[1]", "Описание[1]", Status.DONE, epic1.getId());
        Subtask subtask2 = new Subtask("Подзадача[2]", "Описание[2]", Status.IN_PROGRESS, epic1.getId());
        Subtask subtask3 = new Subtask("Подзадача[3]", "Описание[3]", Status.IN_PROGRESS, epic1.getId());

        taskManager.addSubTask(subtask1);
        taskManager.addSubTask(subtask2);
        taskManager.addSubTask(subtask3);

        PrintConsole printConsole = new PrintConsole();

        List<Task> tasks = taskManager.getTasks();
        List<Epic> epics = taskManager.getEpics();
        List<Subtask> subtasks = taskManager.getSubtask();

        printConsole.printTasks(tasks);
        printConsole.printEpics(epics);
        printConsole.printSubTasksInEpic(epic1, subtasks);

        System.out.println("\n----------_SPRINT4_--------------\n");

        taskManager.getTaskById(0);
        System.out.println(taskManager.getHistory());

        taskManager.getTaskById(1);
        System.out.println(taskManager.getHistory());

        taskManager.getEpicById(2);
        System.out.println(taskManager.getHistory());

        taskManager.getTaskById(0);
        System.out.println(taskManager.getHistory());

        taskManager.getEpicById(3);
        System.out.println(taskManager.getHistory());

        taskManager.getSubtaskById(4);
        System.out.println(taskManager.getHistory());

        taskManager.getSubtaskById(5);
        System.out.println(taskManager.getHistory());

        taskManager.getEpicById(3);
        System.out.println(taskManager.getHistory());

        taskManager.getSubtaskById(6);
        System.out.println(taskManager.getHistory());

        System.out.println("\n----------_SPRINT5_--------------\n");

        //taskManager.deleteEpicInIds(3);
        //taskManager.deleteEpicInIds(2);
        //taskManager.taskAllDelete();
        //taskManager.epicAllDelete();
        //taskManager.subtaskAllDelete();

        System.out.println(taskManager.getHistory());
    }
}
