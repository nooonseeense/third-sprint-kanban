import constants.Status;
import service.TaskManagerService;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

public class Main {

    public static void main(String[] args) {
        TaskManagerService service = new TaskManagerService();

        Task task1 = new Task("Task 1", "Описание", Status.NEW);
        Task task2 = new Task("Task 2", "Описание", Status.NEW);

        service.addTask(task1);
        service.addTask(task2);

        Epic epic1 = new Epic("Построить дом", "Будем строить дом", Status.NEW);
        Epic epic2 = new Epic("Сходить в магазин", "Будем строить дом", Status.NEW);

        service.addEpic(epic1);
        service.addEpic(epic2);

        Subtask subtask1 = new Subtask("Заполнить бетон", "Описание", Status.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Купить шампунь", "Описание", Status.NEW, epic2.getId());

        service.addSubTask(subtask1);
        service.addSubTask(subtask2);
    }
}
