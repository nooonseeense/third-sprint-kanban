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
            client.put("tasks", tasks);

            String subtasks = gson.toJson(super.getSubtasks());
            client.put("subtasks", subtasks);

            String epic = gson.toJson(super.getEpics());
            client.put("epics", epic);

            String history = gson.toJson(super.getHistory());
            client.put("history", history);

            String prioritizedTasks = gson.toJson(super.getPrioritizedTasks());
            client.put("prioritizedTasks", prioritizedTasks);

            String lastTaskId = gson.toJson(getGenerator());
            client.put("lastTaskId", lastTaskId);
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
            HashMap<Integer, Task> tasksFromKVServer = gson.fromJson(client.load("tasks"), hashMapType);
            if (!tasksFromKVServer.isEmpty()) {
                tasks.putAll(tasksFromKVServer);
            }

            HashMap<Integer, Epic> epicsFromKVServer = gson.fromJson(client.load("epics"), hashMapType);
            if (!epicsFromKVServer.isEmpty()) {
                epics.putAll(epicsFromKVServer);
            }

            HashMap<Integer, Subtask> subtasksFromKVServer = gson.fromJson(client.load("subtasks"), hashMapType);
            if (!subtasksFromKVServer.isEmpty()) {
                subtasks.putAll(subtasksFromKVServer);
            }

            List<Task> historyFromKVServer = gson.fromJson(client.load("history"), listType);
            if (!historyFromKVServer.isEmpty()) {
                for (Task task : historyFromKVServer) {
                    historyManager.add(task);
                }
            }

            List<Task> prioritizedTasksFromKVServer = gson.fromJson(client.load("prioritizedTasks"), listType);
            if (!prioritizedTasksFromKVServer.isEmpty()) {
                sortedListTasksAndSubtasks.addAll(prioritizedTasksFromKVServer);
            }

            Integer lastTaskId = gson.fromJson(client.load("lastTaskId"), Integer.class);
            if (lastTaskId != null) {
                setGenerator(lastTaskId);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
