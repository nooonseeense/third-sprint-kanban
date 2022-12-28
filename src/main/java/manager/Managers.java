package manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import http_service.HttpTaskManager;
import service.LocalDateTimeAdapter;
import service.TaskAdapter;
import tasks.Task;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;

public class Managers {

    private Managers() {}

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefaultTask() throws MalformedURLException {
        return new HttpTaskManager(new URL("http://localhost"));
    }

    public static Gson getGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Task.class, new TaskAdapter())
                .create();
    }
}