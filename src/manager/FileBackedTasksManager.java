package manager;

import constants.Status;
import constants.TaskType;
import exceptions.ManagerSaveException;
import service.TasksIdComparator;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import java.io.*;
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

        fileBackedTasksManager.runLoadFromFile();
    }


    public void runLoadFromFile() {
        loadFromFile(file);
    }

    /**
     * Сохраняет текущее состояние менеджера в указанный файл при вызове методов добавления
     */
    private void save() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true))) {
            TasksIdComparator tasksIdComparator = new TasksIdComparator();
            List<Task> sortedTasksById = new LinkedList<>();

            sortedTasksById.addAll(tasks.values());
            sortedTasksById.addAll(epics.values());
            sortedTasksById.addAll(subtasks.values());

            sortedTasksById.sort(tasksIdComparator);

            bufferedWriter.write("id,type,name,status,description,epic\n");
            addTasksToFile(bufferedWriter, sortedTasksById);
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

    /*Восстанавливает данные из файла*/
    // 1. Добавить методы addTusk и тд
    public void loadFromFile(File file) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {

            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();

                // Сделать проверку на пустую строчку
                if (!line.trim().isEmpty() && !line.contains("id,type,name,status,description,epic")) {
                    Task receivedTask = fromString(line);

                    switch (receivedTask.getTaskType()) {
                        case TASK:
                            // Возникает рекурсия так как срабатывает метод save()

                        case SUBTASK:

                        case EPIC:


                    }
                } else {
                    // 2. ЕСЛИ СТРОЧКА С ИТСТОРИЕЙ: Логика записи истории
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
    }

    /*Создание задачи из строки, записанной в методе save()*/
    public Task fromString(String value) {
        String[] split = value.split(",");
        int id = 0;
        TaskType type = null;
        String name = " ";
        Status status = null;
        String description = " ";
        int epicId = 0;

        for (int i = 0; i < split.length; i++) {
            id = Integer.parseInt(split[0]);
            type = TaskType.valueOf(split[1]);
            name = split[2];
            status = Status.valueOf(split[3]);
            description = split[4];

            if (split.length == 6) {
                epicId = Integer.parseInt(split[5]);
            }
            if (type == TaskType.TASK) {
                return new Task(id, type, name, status, description);
            }
            if (type == TaskType.SUBTASK) {
                return new Subtask(id, type, name, status, description, epicId);
            }
            // ошибка с выходом
        }
        return new Epic(id, type, name, status, description);
    }

    // методы getTask, getEpic, getSubtask
    /*Объект считывает строчки историй из файла и записывает в лист истории*/
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