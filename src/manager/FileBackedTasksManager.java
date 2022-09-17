package manager;

import constants.Status;
import exceptions.ManagerSaveException;
import service.TasksIDComparator;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTasksManager implements TasksManager {
    private final File file;

    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    public static void main(String[] args) {
        final String HOME = "src/data/data.csv";

        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(new File(HOME));

        Task task1 = new Task("Задача[1]", "Описание[1]", Status.NEW);
        fileBackedTasksManager.addTask(task1);
        fileBackedTasksManager.getTaskById(task1.getId());

        Epic epic1 = new Epic("Епик[1]", "Описание[Епик]");
        fileBackedTasksManager.addEpic(epic1);
        fileBackedTasksManager.getEpicById(epic1.getId());

        Subtask subtask1 = new Subtask("Подзадача[1]", "Описание[Саб]", Status.DONE, epic1.getId());
        fileBackedTasksManager.addSubTask(subtask1);

        Task task2 = new Task("Задача[2]", "Описание[2]", Status.IN_PROGRESS);
        fileBackedTasksManager.addTask(task2);


    }

    /**
     * Сохраняет текущее состояние менеджера в указанный файл при вызове методов добавления
     */
    private void save() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true))) {
            TasksIDComparator tasksIDComparator = new TasksIDComparator();
            List<Task> sortedTasks = new LinkedList<>();

            sortedTasks.addAll(tasks.values());
            sortedTasks.addAll(epics.values());
            sortedTasks.addAll(subtasks.values());

            sortedTasks.sort(tasksIDComparator);

            bufferedWriter.write("id,type,name,status,description,epic\n");
            addTasksToFile(bufferedWriter, sortedTasks);
            bufferedWriter.write(
                    "\n"
                    + historyToString(historyManager)
                    + "\n"
                    + "\n");
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
    }

    private <T extends Task> void addTasksToFile(BufferedWriter bufferedWriter, Collection<T> tasks)
            throws IOException {
        for (T task : tasks) {
            bufferedWriter.write(task.toString());
        }
    }

    public static String historyToString(HistoryManager historyManager) {
        StringBuilder stringBuilder = new StringBuilder();

        for (Task record : historyManager.getHistory()) {
            stringBuilder.append(record.getId()).append(",");
        }
        return stringBuilder.toString();
    }

    public Task fromString() {

        return null;
    }

    public static List<Integer> historyFromString(String value) {

        return null;
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubTask(Subtask subtask) {
        super.addSubTask(subtask);
        save();
    }
}