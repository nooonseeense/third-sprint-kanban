import constants.Status;
import service.TaskManagerService;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

public class Main {

    public static void main(String[] args) {
        TaskManagerService service = new TaskManagerService();

        Task task1 = new Task("Task 1", "Описание", Status.NEW);

        Epic epic1 = new Epic("Построить дом", "Будем строить дом", Status.NEW);

        Subtask subtask1 = new Subtask(
                "Заполнить бетон",
                "Закладываем бетон",
                Status.NEW,
                epic1.getId());


        service.addTask(task1);
        service.addEpic(epic1);
        service.addSubTask(subtask1);
    }
}
