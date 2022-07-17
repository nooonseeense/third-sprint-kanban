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

        Epic epic1 = new Epic("Построить дом", "Купить бетон", Status.NEW);
        Epic epic2 = new Epic("Сходить в магазин", "Купить хлеб", Status.NEW);

        service.addEpic(epic1);
        service.addEpic(epic2);

        Subtask subtask1 = new Subtask("Заполнить бетон", "Оп", Status.DONE, epic1.getId());
        Subtask subtask2 = new Subtask("Купить шампунь", "Оп_2", Status.IN_PROGRESS, epic2.getId());

        service.addSubTask(subtask1);
        service.addSubTask(subtask2);

        PrintConsoleService printConsoleService = new PrintConsoleService();

        List<Task> tasks = service.getTasks();
        List<Epic> epics = service.getEpics();
        List<Subtask> subtasks = service.getSubtask();

        printConsoleService.printTasks(tasks);
        printConsoleService.printEpics(epics);

        service.getTaskById(1);
        service.getEpicById(2);
        service.getSubtaskById(4);

        Task task3 = new Task("Task 3 (ОБНОВЛЕННЫЙ)", "Описание)", Status.NEW);
        service.updateTask(task3, 0);



        service.deleteSubTaskInIds(epic1, 4);

        printConsoleService.printSubTasksInEpic(epic1, subtasks);
    }
}
