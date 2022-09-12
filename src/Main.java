import constants.Status;
import manager.Managers;
import manager.TasksManager;
import service.PrintConsole;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        TasksManager tasksManager = Managers.getDefaultTask();

        System.out.println("\n----------_SPRINT3_--------------\n");

        Task task1 = new Task("Задача[1]", "Описание[1]", Status.NEW);
        Task task2 = new Task("Задача[2]", "Описание[2]", Status.IN_PROGRESS);

        tasksManager.addTask(task1);
        tasksManager.addTask(task2);

        Epic epic1 = new Epic("Епик[1]", "Описание[1]");
        Epic epic2 = new Epic("Епик[2]", "Описание[2]");

        tasksManager.addEpic(epic1);
        tasksManager.addEpic(epic2);

        Subtask subtask1 = new Subtask("Подзадача[1]", "Описание[1]", Status.DONE, epic1.getId());
        Subtask subtask2 = new Subtask("Подзадача[2]", "Описание[2]", Status.IN_PROGRESS, epic1.getId());
        Subtask subtask3 = new Subtask("Подзадача[3]", "Описание[3]", Status.IN_PROGRESS, epic1.getId());

        tasksManager.addSubTask(subtask1);
        tasksManager.addSubTask(subtask2);
        tasksManager.addSubTask(subtask3);

        PrintConsole printConsole = new PrintConsole();

        List<Task> tasks = tasksManager.getTasks();
        List<Epic> epics = tasksManager.getEpics();
        List<Subtask> subtasks = tasksManager.getSubtask();

        printConsole.printTasks(tasks);
        printConsole.printEpics(epics);
        printConsole.printSubTasksInEpic(epic1, subtasks);

        System.out.println("\n----------_SPRINT4_--------------\n");

        tasksManager.getTaskById(0);
        System.out.println(tasksManager.getHistory());

        tasksManager.getTaskById(1);
        System.out.println(tasksManager.getHistory());

        tasksManager.getEpicById(2);
        System.out.println(tasksManager.getHistory());

        tasksManager.getTaskById(0);
        System.out.println(tasksManager.getHistory());

        tasksManager.getEpicById(3);
        System.out.println(tasksManager.getHistory());

        tasksManager.getSubtaskById(4);
        System.out.println(tasksManager.getHistory());

        tasksManager.getSubtaskById(5);
        System.out.println(tasksManager.getHistory());

        tasksManager.getEpicById(3);
        System.out.println(tasksManager.getHistory());

        tasksManager.getSubtaskById(6);
        System.out.println(tasksManager.getHistory());

        System.out.println("\n----------_SPRINT5_--------------\n");

        tasksManager.deleteEpicInIds(3);
        tasksManager.deleteEpicInIds(2);
        tasksManager.taskAllDelete();
        tasksManager.epicAllDelete();
        tasksManager.subtaskAllDelete();

        System.out.println("ДАННЫЕ УДАЛЕНЫ ИЗ ИСТОРИИ ПРОСМОТРА: " + tasksManager.getHistory());

        System.out.println("\n----------_SPRINT6_--------------\n");



//        fileBackedTasksManager.addTask(task1);
//        fileBackedTasksManager.addEpic(epic1);
//        fileBackedTasksManager.addSubTask(subtask1);

    }
}