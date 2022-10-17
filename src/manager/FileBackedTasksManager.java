package manager;

import constants.Status;
import constants.TaskType;
import exceptions.ManagerSaveException;
import service.TasksIdComparator;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import java.io.*;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTasksManager implements TasksManager {
    private final File file;

    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    public static void main(String[] args) {
        final String HOME = "src/data/data.csv";

        FileBackedTasksManager fileBackedTasksManager = FileBackedTasksManager.loadFromFile(new File(HOME));

        Task task1 = new Task("Задача[1]", "Описание[1]", Status.NEW);
        fileBackedTasksManager.addTask(task1);
        fileBackedTasksManager.getTaskById(task1.getId());

        Epic epic1 = new Epic("Епик[1]", "Описание[Епик]");
        fileBackedTasksManager.addEpic(epic1);
        fileBackedTasksManager.getEpicById(epic1.getId());

        Subtask subtask1 = new Subtask("Подзадача[1]", "Описание[Саб]", Status.DONE, epic1.getId());
        fileBackedTasksManager.addSubTask(subtask1);

    }

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

    public static FileBackedTasksManager loadFromFile(File file) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);
            InMemoryTasksManager inMemoryTasksManager = new InMemoryTasksManager();
            HistoryManager historyManager = Managers.getDefaultHistory();
            Map<Integer, Task> tempStorageOfTasks = new HashMap<>();

            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();

                if (line.contains("id,type,name,status,description,epic") || line.equals("")) {
                    continue;
                }

                if (line.contains("TASK") || line.contains("EPIC") || line.contains("SUBTASK")) {
                    Task receivedTask = fileBackedTasksManager.fromString(line);

                    switch (receivedTask.getTaskType()) {
                        case TASK:
                            inMemoryTasksManager.tasks.put(receivedTask.getId(), receivedTask);
                            tempStorageOfTasks.put(receivedTask.getId(), receivedTask);
                            break;
                        case SUBTASK:
                            inMemoryTasksManager.subtasks.put(receivedTask.getId(), (Subtask) receivedTask);
                            tempStorageOfTasks.put(receivedTask.getId(), receivedTask);
                            break;
                        case EPIC:
                            inMemoryTasksManager.epics.put(receivedTask.getId(), (Epic) receivedTask);
                            tempStorageOfTasks.put(receivedTask.getId(), receivedTask);
                            break;
                    }
                } else {
                    for (int historyId : historyFromString(line)) {
                        historyManager.add(tempStorageOfTasks.get(historyId));
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
        return new FileBackedTasksManager(file);
    }

    public Task fromString(String value) {
        String[] valueSplit = value.split(",");
        int id = Integer.parseInt(valueSplit[0]);
        TaskType type = TaskType.valueOf(valueSplit[1]);
        String name = valueSplit[2];
        Status status = Status.valueOf(valueSplit[3]);
        String description = valueSplit[4];

        if (type == TaskType.TASK) {
            return new Task(id, type, name, status, description);
        }
        if (type == TaskType.SUBTASK) {
            return new Subtask(id, type, name, status, description, Integer.parseInt(valueSplit[5]));
        }
        return new Epic(id, type, name, status, description);
    }

    public static List<Integer> historyFromString(String value) {
        String[] valueSplit = value.split(",");
        List<Integer> historyIds = new LinkedList<>();

        for (String num : valueSplit) {
            historyIds.add(Integer.parseInt(num));
        }
        return historyIds;
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