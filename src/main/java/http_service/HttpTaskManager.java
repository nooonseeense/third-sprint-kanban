package http_service;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import manager.FileBackedTaskManager;
import manager.Managers;

import java.net.URL;

public class HttpTaskManager extends FileBackedTaskManager { // ТРЕТЬЯ СЕРИАЛИЗИАЦИЯ МЕТОД SAVE БУДЕТ ИСПОЛЬЗОВАТЬСЯ В FileBackedTaskManager от сюда вызывать его не нужно
    private final URL url;
    private final KVTaskClient client;
    private final Gson gson;

    public HttpTaskManager(URL url) {
        this.url = url;
        this.client = new KVTaskClient(url);
        this.gson = Managers.getGson();
    }

    @Override
    public void save() {
        String key = url.getFile();

        try {
            switch (key) {
                case "/tasks/task":
                    String tasks = gson.toJson(getTasks());
                    client.put(key, tasks);
                    break;
                case "/tasks/subtask":
                    String subtasks = gson.toJson(getSubtasks());
                    client.put(key, subtasks);
                    break;
                case "/tasks/epic":
                    String epic = gson.toJson(getEpics());
                    client.put(key, epic);
                    break;
                case "/tasks/history":
                    String history = gson.toJson(getHistory());
                    client.put(key, history);
                    break;
                case "/tasks":
                    String prioritizedTasks = gson.toJson(getPrioritizedTasks());
                    client.put(key, prioritizedTasks);
                    break;
            }
        } catch (JsonParseException e) {
            e.printStackTrace();
        }
    }
}
