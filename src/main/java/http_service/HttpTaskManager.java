package http_service;

import manager.FileBackedTaskManager;
import java.net.URL;

public class HttpTaskManager extends FileBackedTaskManager {
    private final URL url;
    private final KVTaskClient client;

    public HttpTaskManager(URL url) { // TODO 1. Сделать конструктор, который принимает URL к серверу KVServer
        this.url = url;
        this.client = new KVTaskClient(url);
    }

    @Override
    public void save() {
        // TODO 2. Изменить реализацию
        // Метод будет пользоваться kvTaskClient и передавать в него наши задачи за счет метода put
        super.save();
    }
}
