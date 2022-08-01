import constants.Status;
import manager.HistoryManager;
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

        Task task1 = new Task("Task 1", "Описание", Status.NEW);
        Task task2 = new Task("Task 2", "Это Task2", Status.IN_PROGRESS);

        taskManager.addTask(task1);
        taskManager.addTask(task2);

        Epic epic1 = new Epic("Построить дом", "Купить бетон");
        Epic epic2 = new Epic("Сходить в магазин", "Купить хлеб");
        Epic epic4 = new Epic("Откопать сокровище", "Особо ценное");

        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.addEpic(epic4);

        Subtask subtask1 = new Subtask("Заполнить бетон", "Оп", Status.DONE, epic1.getId());
        Subtask subtask2 = new Subtask("Купить шампунь", "Оп_2", Status.IN_PROGRESS, epic2.getId());
        Subtask subtask4 = new Subtask("Оборвать веревку", "()", Status.IN_PROGRESS, epic4.getId());

        taskManager.addSubTask(subtask1);
        taskManager.addSubTask(subtask2);
        taskManager.addSubTask(subtask4);

        PrintConsole printConsole = new PrintConsole();

        List<Task> tasks = taskManager.getTasks();
        List<Epic> epics = taskManager.getEpics();
        List<Subtask> subtasks = taskManager.getSubtask();

        printConsole.printTasks(tasks);
        printConsole.printEpics(epics);
        printConsole.printSubTasksInEpic(epic4, subtasks);

        System.out.println("\n----------Тестируем метод getHistory()--------------\n");

        taskManager.getTaskById(0);
        System.out.println(taskManager.getHistory());

        taskManager.getEpicById(2);
        System.out.println(taskManager.getHistory());

        taskManager.getSubtaskById(6);
        System.out.println(taskManager.getHistory());



//        Task task3 = new Task("Task 3 (ОБНОВЛЕННЫЙ)", "(Описание)", Status.NEW);
//        Task task4 = new Task("Task 4 (ОБНОВЛЕННЫЙ_2)", "(Описание_2)", Status.NEW);
//
//        task3.setId(task2.getId());
//        taskManager.updateTask(task3);
//        taskManager.updateTask(task4);
//
//        tasks.add(task3);
//        tasks.add(task4);

       taskManager.deleteTaskInIds(0);
//        taskManager.deleteEpicInIds(2);
//        taskManager.deleteSubTaskInIds(6);

    }
}
