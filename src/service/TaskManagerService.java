package service;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import java.util.HashMap;

public class TaskManagerService {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Task> subTasks = new HashMap<>();
    private HashMap<Integer, Task> epics = new HashMap<>();
    private int generator = 0;

    public void addTask(Task task) {
        int taskId = generator++;
        task.setId(taskId);
        tasks.put(taskId, task);
    }

    public void addSubTask(Subtask subtask) {
        int subtaskId = generator++;
        subtask.setId(subtaskId);
        subTasks.put(subtaskId, subtask);
    }

    public void addEpic(Epic epic) {
        int epicId = generator++;
        epic.setId(epicId);
        epics.put(epicId,epic);
    }
}
