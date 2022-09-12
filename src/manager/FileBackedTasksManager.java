package manager;

import constants.Status;
import constants.TaskType;
import exceptions.ManagerSaveException;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileBackedTasksManager extends InMemoryTasksManager implements TasksManager {
    private final File file;

    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    public static void main(String[] args) {
        final String HOME = "src/data/data.csv";

        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(new File(HOME));

        System.out.println("\n----------_SPRINT6_--------------\n");

        Task task1 = new Task("Задача[1]", "Описание[1]", Status.NEW);
        Epic epic1 = new Epic("Епик[1]", "Описание[1]");
        Subtask subtask1 = new Subtask("Подзадача[1]", "Описание[1]", Status.DONE, epic1.getId());

        fileBackedTasksManager.addTask(task1);
        fileBackedTasksManager.addEpic(epic1);
        fileBackedTasksManager.addSubTask(subtask1);
    }

    /**
     * Сохраняет текущее состояние менеджера в указанный файл при вызове методов добавления
     */
    private <T extends Task> void save(T task) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true))) {
            addTasksToFile(bufferedWriter, task);
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
    }

    private <T extends Task> void addTasksToFile(BufferedWriter bufferedWriter, T task)
            throws IOException {

        if (task.getTaskType().equals(TaskType.SUBTASK)) {
            bufferedWriter.write(toString(task));
        }
        if (task.getTaskType().equals(TaskType.TASK)) {
            bufferedWriter.write(toString(task));
        }
        if (task.getTaskType().equals(TaskType.EPIC)) {
            bufferedWriter.write(toString(task));
        }
    }

    public String toString(Task task) {
        return task.getId() + ","
                + task.getTaskType() + ","
                + task.getName() + ","
                + task.getStatus() + ","
                + task.getDescription() + "\n";
    }

    /**
     * Сначала выполняется версия, унаследованная от предка, а затем метод save()
     */
    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save(task);
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save(epic);
    }

    @Override
    public void addSubTask(Subtask subtask) {
        super.addSubTask(subtask);
        save(subtask);
    }
}
