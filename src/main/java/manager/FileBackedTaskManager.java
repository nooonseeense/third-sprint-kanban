package manager;

import constants.Status;
import constants.TaskType;
import exceptions.ManagerSaveException;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.*;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private static final String HEADER = "id,type,name,status,description,duration,startTime,endTime,epic";
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static void main(String[] args) {
        final String HOME = "src/main/java/data/data.csv";

        FileBackedTaskManager fileBackedTasksManager = FileBackedTaskManager.loadFromFile(new File(HOME));

        Task task1 = new Task("Задача", "Описание", Status.NEW, 60,
                LocalDateTime.of(2022, Month.NOVEMBER, 2, 14, 30));
        fileBackedTasksManager.addTask(task1);
        fileBackedTasksManager.getTaskById(task1.getId());

        Task task2 = new Task("Задача", "Описание", Status.NEW, 120, LocalDateTime.of(2020, Month.AUGUST, 20, 20, 40));
        fileBackedTasksManager.addTask(task2);
        fileBackedTasksManager.getTaskById(task2.getId());

        Epic epic1 = new Epic("Епик[1]", "Описание[Епик]");
        fileBackedTasksManager.addEpic(epic1);
        fileBackedTasksManager.getEpicById(epic1.getId());

        Subtask subtask1 = new Subtask("Подзадача[1]", "Описание[2]", Status.IN_PROGRESS,
                epic1.getId());
        fileBackedTasksManager.addSubtask(subtask1);
        fileBackedTasksManager.getSubtaskById(subtask1.getId());

        Subtask subtask3 = new Subtask("Подзадача[3]", "Описание[3]", Status.IN_PROGRESS,
                120, LocalDateTime.of(2020, Month.AUGUST, 20, 23, 50), epic1.getId());
        fileBackedTasksManager.addSubtask(subtask3);
        fileBackedTasksManager.getSubtaskById(subtask3.getId());

        Subtask subtask4 = new Subtask("Подзадача[2]", "Описание[2]", Status.IN_PROGRESS,
                40, LocalDateTime.of(2020, Month.AUGUST, 20, 20, 50), epic1.getId());
        fileBackedTasksManager.addSubtask(subtask4);
        fileBackedTasksManager.getSubtaskById(subtask4.getId());

        Subtask subtask5 = new Subtask("Подзадача[2]", "Описание[2]", Status.IN_PROGRESS,
                epic1.getId());
        fileBackedTasksManager.addSubtask(subtask5);
        fileBackedTasksManager.getSubtaskById(subtask5.getId());

        Subtask subtask6 = new Subtask("Подзадача[4]", "Описание[4]", Status.IN_PROGRESS,
                120, LocalDateTime.of(2021, Month.AUGUST, 20, 20, 30), epic1.getId());
        fileBackedTasksManager.addSubtask(subtask6);
        fileBackedTasksManager.getSubtaskById(subtask6.getId());
    }

    private void save() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true))) {
            List<Task> sortedTasksById = new LinkedList<>();

            sortedTasksById.addAll(tasks.values());
            sortedTasksById.addAll(epics.values());
            sortedTasksById.addAll(subtasks.values());
            sortedTasksById.sort(Comparator.comparingInt(Task::getId));

            bufferedWriter.write(HEADER + "\n");
            addTasksToFile(bufferedWriter, sortedTasksById);
            bufferedWriter.write("\n" + historyToString(historyManager) + "\n" + "\n");
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

    private static String historyToString(HistoryManager historyManager) {
        StringBuilder stringBuilder = new StringBuilder();
        List<Task> taskHistory = historyManager.getHistory();

        for (int i = 0; i < taskHistory.size(); i++) {
            if (i == taskHistory.size() - 1) {
                stringBuilder.append(taskHistory.get(i).getId());
                break;
            }
            stringBuilder.append(taskHistory.get(i).getId()).append(",");
        }
        return stringBuilder.toString();
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            FileBackedTaskManager fileBackedTasksManager = new FileBackedTaskManager(file);
            InMemoryTaskManager inMemoryTasksManager = new InMemoryTaskManager();
            HistoryManager historyManager = Managers.getDefaultHistory();
            Map<Integer, Task> tempStorageOfTasks = new HashMap<>();


            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();

                if (line.contains(HEADER) || line.equals("")) {
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
        return new FileBackedTaskManager(file);
    }

    private Task fromString(String value) {
        String[] valueSplit = value.split(",");

        int id = Integer.parseInt(valueSplit[0]);
        TaskType type = TaskType.valueOf(valueSplit[1]);
        String name = valueSplit[2];
        Status status = Status.valueOf(valueSplit[3]);
        String description = valueSplit[4];
        int duration = Integer.parseInt(valueSplit[5]);
        LocalDateTime startTime;
        LocalDateTime endTime;

        if (!(valueSplit[6].equals("null"))) {
            startTime = LocalDateTime.parse(valueSplit[6]);
            endTime = LocalDateTime.parse(valueSplit[7]);
        } else {
            startTime = null;
            endTime = null;
        }

        switch (type) {
            case TASK:
                return new Task(id, type, name, status, description, duration, startTime, endTime);
            case SUBTASK:
                return new Subtask(
                        id,
                        type,
                        name,
                        status,
                        description,
                        duration,
                        startTime,
                        endTime,
                        Integer.parseInt(valueSplit[8])
                );
        }
        return new Epic(id, type, name, status, description, duration, startTime, endTime);
    }

    private static List<Integer> historyFromString(String value) {
        List<Integer> historyIds = new LinkedList<>();

        if (!value.isBlank()) {
            String[] valueSplit = value.split(",");

            for (String num : valueSplit) {
                historyIds.add(Integer.parseInt(num));
            }
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
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        save();
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        save();
        return subtask;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void taskAllDelete() {
        super.taskAllDelete();
        save();
    }

    @Override
    public void epicAllDelete() {
        super.epicAllDelete();
        save();
    }

    @Override
    public void subtaskAllDelete() {
        super.subtaskAllDelete();
        save();
    }

    @Override
    public void deleteTaskInIds(int id) {
        super.deleteTaskInIds(id);
        save();
    }

    @Override
    public void deleteEpicInIds(int id) {
        super.deleteEpicInIds(id);
        save();
    }

    @Override
    public void deleteSubTaskInIds(int id) {
        super.deleteSubTaskInIds(id);
        save();
    }
}