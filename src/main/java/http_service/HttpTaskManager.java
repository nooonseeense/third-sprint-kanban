package http_service;

import com.google.gson.Gson;
import manager.FileBackedTaskManager;
import manager.Managers;
import java.net.URL;
import java.util.ArrayList;

public class HttpTaskManager extends FileBackedTaskManager {
    private final URL url;
    private final KVTaskClient client;
    private final Gson gson;

    public HttpTaskManager(URL url) {
        this.url = url;
        this.client = new KVTaskClient(url);
        this.gson = Managers.getGson();
    }

    @Override
    public void save() { // Метод save(), который лежит в FileBackTaskManager
        // TODO 2. Изменить реализацию
        // Метод будет пользоваться kvTaskClient и передавать в него наши задачи за счет метода put
        // Сохранить таски
        String tasksJson = gson.toJson(new ArrayList<>(getAllTasks()));
        client.put(url.getFile(), tasksJson);
    }
}
