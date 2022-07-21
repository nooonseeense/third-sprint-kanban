import constants.Status;
import service.PrintConsoleService;
import service.TaskManagerService;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        TaskManagerService service = new TaskManagerService();

        Task task1 = new Task("Task 1", "Описание", Status.NEW);
        Task task2 = new Task("Task 2", "Описание", Status.IN_PROGRESS);

        service.addTask(task1);
        service.addTask(task2);

        Epic epic1 = new Epic("Построить дом", "Купить бетон");
        Epic epic2 = new Epic("Сходить в магазин", "Купить хлеб");
        Epic epic4 = new Epic("Откопать сокровище", "Особо ценное");

        service.addEpic(epic1);
        service.addEpic(epic2);
        service.addEpic(epic4);

        Subtask subtask1 = new Subtask("Заполнить бетон", "Оп", Status.DONE, epic1.getId());
        Subtask subtask2 = new Subtask("Купить шампунь", "Оп_2", Status.IN_PROGRESS, epic2.getId());
        Subtask subtask4 = new Subtask("Оборвать веревку", "()", Status.IN_PROGRESS, epic4.getId());

        service.updateSubtask(subtask4);
        service.addSubTask(subtask1);
        service.addSubTask(subtask2);

        PrintConsoleService printConsoleService = new PrintConsoleService();

        List<Task> tasks = service.getTasks();
        List<Epic> epics = service.getEpics();
        List<Subtask> subtasks = service.getSubtask();

        printConsoleService.printTasks(tasks);
        printConsoleService.printEpics(epics);
        printConsoleService.printSubTasksInEpic(epic4, subtasks);

        service.getTaskById(1);
        service.getEpicById(2);
        service.getSubtaskById(4);

        Task task3 = new Task("Task 3 (ОБНОВЛЕННЫЙ)", "(Описание)", Status.NEW);
        Task task4 = new Task("Task 4 (ОБНОВЛЕННЫЙ_2)", "(Описание_2)", Status.NEW);

        task3.setId(task2.getId());
        service.updateTask(task3);
        service.updateTask(task4);

        tasks.add(task3);
        tasks.add(task4);

        service.deleteSubTaskInIds(5);
        printConsoleService.printSubTasksInEpic(epic1, subtasks);
        service.deleteEpicInIds(2);



    }
}
