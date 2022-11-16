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

public class FileBackedTasksManager extends InMemoryTasksManager {
    private final File file;

    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    public static void main(String[] args) {
        final String HOME = "src/main/java/data/data.csv";

        FileBackedTasksManager fileBackedTasksManager = FileBackedTasksManager.loadFromFile(new File(HOME));

        Task task1 = new Task("Задача", "Описание",
                Status.NEW, 60, LocalDateTime.of(2022, Month.NOVEMBER, 2, 14, 30));
        fileBackedTasksManager.addTask(task1);
        fileBackedTasksManager.getTaskById(task1.getId());

        Epic epic1 = new Epic("Епик[1]", "Описание[Епик]");
        fileBackedTasksManager.addEpic(epic1);
        fileBackedTasksManager.getEpicById(epic1.getId());

        Subtask subtask1 = new Subtask("Подзадача[1]", "Описание[2]", Status.DONE,
                60, LocalDateTime.of(2020, Month.JULY, 14, 10, 0), epic1.getId());
        fileBackedTasksManager.addSubTask(subtask1);
        fileBackedTasksManager.getSubtaskById(subtask1.getId());

        Subtask subtask2 = new Subtask("Подзадача[2]", "Описание[2]", Status.IN_PROGRESS,
                60, LocalDateTime.of(2020, Month.AUGUST, 20, 20, 30), epic1.getId());
        fileBackedTasksManager.addSubTask(subtask2);
        fileBackedTasksManager.getSubtaskById(subtask2.getId());


    }

    private void save() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true))) {
            List<Task> sortedTasksById = new LinkedList<>();

            sortedTasksById.addAll(tasks.values());
            sortedTasksById.addAll(epics.values());
            sortedTasksById.addAll(subtasks.values());

            sortedTasksById.sort(Comparator.comparingInt(Task::getId));

            bufferedWriter.write("id,type,name,status,description,duration,startTime,endTime,epic\n");
            addTasksToFile(bufferedWriter, sortedTasksById);
            bufferedWriter.write("\n" + historyToString(historyManager) + "\n" + "\n");
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
    }

    private <T extends Task> void addTasksToFile(BufferedWriter bufferedWriter, Collection<T> tasks)
            throws IOException {
        for (T task : tasks) {
            if (task.getStartTime() == null || task.getEndTime() == null) {
                continue;
            }
            bufferedWriter.write(task.toString());
        }
    }

//    private <T extends Task> void addTasksToFile(BufferedWriter bufferedWriter, Collection<T> tasks)
//            throws IOException {
//        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
//
//        for (T task : tasks) {
//            String[] taskLine = task.toString().split(",");
//
//            int id = Integer.parseInt(taskLine[0]);
//            TaskType type = TaskType.valueOf(taskLine[1]);
//            String name = taskLine[2];
//            Status status = Status.valueOf(taskLine[3]);
//            String description = taskLine[4];
//            int duration = Integer.parseInt(taskLine[5]);
//            String startTime = task.getStartTime().format(dateTimeFormatter);
//            String endTime = task.getEndTime().format(dateTimeFormatter);
//
//            if (task.getTaskType() == TaskType.EPIC) {
//                bufferedWriter.write(
//                        id + ","
//                                + type + ","
//                                + name + ","
//                                + status + ","
//                                + description + ","
//                                + duration + ","
//                                + startTime + ","
//                                + endTime + ","
//                                + taskLine[8] + "\n"
//                );
//            } else {
//                bufferedWriter.write(
//                        id + ","
//                                + type + ","
//                                + name + ","
//                                + status + ","
//                                + description + ","
//                                + duration + ","
//                                + startTime + ","
//                                + endTime + ","
//                                + "\n"
//                );
//            }
//        }
//    }

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

                if (line.contains("id,type,name,status,description,duration,startTime,endTime,epic")
                        || line.equals("")
                ) {
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

    public Task fromString(String value) { // Перепарсить строчку
        String[] valueSplit = value.split(",");

        int id = Integer.parseInt(valueSplit[0]);
        TaskType type = TaskType.valueOf(valueSplit[1]);
        String name = valueSplit[2];
        Status status = Status.valueOf(valueSplit[3]);
        String description = valueSplit[4];
        int duration = Integer.parseInt(valueSplit[5]);
        LocalDateTime startTime = LocalDateTime.parse(valueSplit[6]);
        LocalDateTime endTime = LocalDateTime.parse(valueSplit[7]);

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

    @Override
    public Task getTaskById(int id) {
        historyManager.add(tasks.get(id));
        save();
        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        historyManager.add(epics.get(id));
        save();
        return epics.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        historyManager.add(subtasks.get(id));
        save();
        return subtasks.get(id);
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