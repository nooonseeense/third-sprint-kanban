package http_service;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import manager.FileBackedTaskManager;
import manager.Managers;
import tasks.Task;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HttpTaskManager extends FileBackedTaskManager {
    private static final String TASKS_KEY = "tasks";
    private static final String EPICS_KEY = "epics";
    private static final String SUBTASKS_KEY = "subtasks";
    private static final String HISTORY_KEY = "history";
    private static final String PRIORITIZED_TASKS_KEY = "prioritizedTasks";
    private static final String LAST_TASK_ID_KEY = "lastTaskId";

    private final KVTaskClient client;
    private final Gson gson;
    private final URL url;

    public HttpTaskManager(URL url) {
        this.url = url;
        this.client = new KVTaskClient(url);
        this.gson = Managers.getGson();
        load();
    }

    @Override
    public void save() {
        try {
            String tasks = gson.toJson(super.getTasks());
            client.put(TASKS_KEY, tasks);

            String epic = gson.toJson(super.getEpics());
            client.put(EPICS_KEY, epic);

            String subtasks = gson.toJson(super.getSubtasks());
            client.put(SUBTASKS_KEY, subtasks);

            String history = gson.toJson(super.getHistory());
            client.put(HISTORY_KEY, history);

            String prioritizedTasks = gson.toJson(getPrioritizedTasks());
            client.put(PRIORITIZED_TASKS_KEY, prioritizedTasks);

            String lastTaskId = String.valueOf(getGenerator());
            client.put(LAST_TASK_ID_KEY, lastTaskId);
        } catch (JsonParseException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        Type taskType = new TypeToken<ArrayList<Task>>() {
        }.getType();

        try {
            ArrayList<Task> tasksFromKVServer = gson.fromJson(client.load(TASKS_KEY), taskType);
            if (tasksFromKVServer != null && !tasksFromKVServer.isEmpty()) {
                for (Task task : tasksFromKVServer) {
                    tasks.put(task.getId(), task);
                }
            }

            ArrayList<Task> epicsFromKVServer = gson.fromJson(client.load(EPICS_KEY), taskType);
            if (epicsFromKVServer != null && !epicsFromKVServer.isEmpty()) {
                for (Task epic : epicsFromKVServer) {
                    tasks.put(epic.getId(), epic);
                }
            }

            ArrayList<Task> subtasksFromKVServer = gson.fromJson(client.load(SUBTASKS_KEY), taskType);
            if (subtasksFromKVServer != null && !subtasksFromKVServer.isEmpty()) {
                for (Task subtask : subtasksFromKVServer) {
                    tasks.put(subtask.getId(), subtask);
                }
            }

            List<Task> historyFromKVServer = gson.fromJson(client.load(HISTORY_KEY), taskType);
            if (historyFromKVServer != null && !historyFromKVServer.isEmpty()) {
                for (Task task : historyFromKVServer) {
                    historyManager.add(task);
                }
            }

            List<Task> prioritizedTasksFromKVServer = gson.fromJson(client.load(PRIORITIZED_TASKS_KEY), taskType);
            if (prioritizedTasksFromKVServer != null && !prioritizedTasksFromKVServer.isEmpty()) {
                sortedListTasksAndSubtasks.addAll(prioritizedTasksFromKVServer);
            }

            Integer lastTaskId = gson.fromJson(client.load(LAST_TASK_ID_KEY), Integer.class);
            if (lastTaskId != null) {
                setGenerator(lastTaskId);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
