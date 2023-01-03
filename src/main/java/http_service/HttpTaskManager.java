package http_service;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import manager.FileBackedTaskManager;
import manager.Managers;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HttpTaskManager extends FileBackedTaskManager {
    private static final String TASKS_KEY = "TASKS";
    private static final String EPICS_KEY = "EPICS";
    private static final String SUBTASKS_KEY = "SUBTASKS";
    private static final String HISTORY_KEY = "HISTORY";
    private static final String PRIORITIZED_TASKS_KEY = "PRIORITIZED_TASKS";
    private static final String LAST_TASK_ID_KEY = "LAST_TASK_ID";

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

            String prioritizedTasks = gson.toJson(super.getPrioritizedTasks());
            client.put(PRIORITIZED_TASKS_KEY, prioritizedTasks);

            String lastTaskId = gson.toJson(getGenerator());
            client.put(LAST_TASK_ID_KEY, lastTaskId);
        } catch (JsonParseException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        Type hashMapType = new TypeToken<HashMap<Integer, Task>>() {
        }.getType();
        Type listType = new TypeToken<ArrayList<Task>>() {
        }.getType();

        try {
            HashMap<Integer, Task> tasksFromKVServer = gson.fromJson(client.load(TASKS_KEY), hashMapType);
            if (!tasksFromKVServer.isEmpty()) {
                tasks.putAll(tasksFromKVServer);
            }

            HashMap<Integer, Epic> epicsFromKVServer = gson.fromJson(client.load(EPICS_KEY), hashMapType);
            if (!epicsFromKVServer.isEmpty()) {
                epics.putAll(epicsFromKVServer);
            }

            HashMap<Integer, Subtask> subtasksFromKVServer = gson.fromJson(client.load(SUBTASKS_KEY), hashMapType);
            if (!subtasksFromKVServer.isEmpty()) {
                subtasks.putAll(subtasksFromKVServer);
            }

            List<Task> historyFromKVServer = gson.fromJson(client.load(HISTORY_KEY), listType);
            if (!historyFromKVServer.isEmpty()) {
                for (Task task : historyFromKVServer) {
                    historyManager.add(task);
                }
            }

            List<Task> prioritizedTasksFromKVServer = gson.fromJson(client.load(PRIORITIZED_TASKS_KEY), listType);
            if (!prioritizedTasksFromKVServer.isEmpty()) {
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
