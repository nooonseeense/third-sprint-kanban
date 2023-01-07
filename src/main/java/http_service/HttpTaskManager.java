package http_service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import manager.FileBackedTaskManager;
import manager.Managers;
import tasks.Task;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTaskManager {
    private static final String TASKS_KEY = "tasks";
    private static final String EPICS_KEY = "epics";
    private static final String SUBTASKS_KEY = "subtasks";
    private static final String HISTORY_KEY = "history";
    private static final String LAST_TASK_ID_KEY = "lastTaskId";
    private final KVTaskClient client;
    private final Gson gson;

    public HttpTaskManager(URL url, boolean isLoad) {
        this.client = new KVTaskClient(url);
        this.gson = Managers.getGson();

        if (isLoad) {
            load();
        }
    }

    @Override
    public void save() {
        String tasks = gson.toJson(super.getTasks());
        client.put(TASKS_KEY, tasks);

        String epic = gson.toJson(super.getEpics());
        client.put(EPICS_KEY, epic);

        String subtasks = gson.toJson(super.getSubtasks());
        client.put(SUBTASKS_KEY, subtasks);

        String history = gson.toJson(getHistory().stream().map(Task::getId).collect(Collectors.toList()));
        client.put(HISTORY_KEY, history);

        String lastTaskId = String.valueOf(getGenerator());
        client.put(LAST_TASK_ID_KEY, lastTaskId);
    }

    private void load() {
        Type taskType = new TypeToken<ArrayList<Task>>() {
        }.getType();
        Type taskTypeInteger = new TypeToken<ArrayList<Integer>>() {
        }.getType();

        Map<Integer, Task> tempStorageOfTasks = new HashMap<>();
        ArrayList<Task> tasksFromKVServer = gson.fromJson(client.load(TASKS_KEY), taskType);
        if (tasksFromKVServer != null) {
            for (Task task : tasksFromKVServer) {
                tasks.put(task.getId(), task);
                tempStorageOfTasks.put(task.getId(), task);
                sortedListTasksAndSubtasks.add(task);
            }
        }
        ArrayList<Task> epicsFromKVServer = gson.fromJson(client.load(EPICS_KEY), taskType);
        if (epicsFromKVServer != null) {
            for (Task epic : epicsFromKVServer) {
                tasks.put(epic.getId(), epic);
                tempStorageOfTasks.put(epic.getId(), epic);
            }
        }
        ArrayList<Task> subtasksFromKVServer = gson.fromJson(client.load(SUBTASKS_KEY), taskType);
        if (subtasksFromKVServer != null) {
            for (Task subtask : subtasksFromKVServer) {
                tasks.put(subtask.getId(), subtask);
                tempStorageOfTasks.put(subtask.getId(), subtask);
                sortedListTasksAndSubtasks.add(subtask);
            }
        }
        List<Integer> historyFromKVServer = gson.fromJson(client.load(HISTORY_KEY), taskTypeInteger);
        if (historyFromKVServer != null) {
            for (Integer taskId : historyFromKVServer) {
                historyManager.add(tempStorageOfTasks.get(taskId));
            }
        }
        Integer lastTaskId = gson.fromJson(client.load(LAST_TASK_ID_KEY), Integer.class);
        if (lastTaskId != null) {
            setGenerator(lastTaskId);
        }
    }
}
