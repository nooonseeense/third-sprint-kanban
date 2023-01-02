package http_service;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import manager.FileBackedTaskManager;
import manager.Managers;

import java.net.URL;

public class HttpTaskManager extends FileBackedTaskManager {
    private final KVTaskClient client;
    private final Gson gson;

    public HttpTaskManager(URL url) {
        this.client = new KVTaskClient(url);
        this.gson = Managers.getGson();
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
        } catch (JsonParseException e) {
            e.printStackTrace();
        }
    }
}
