package manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import http_service.HttpTaskManager;

import java.io.IOException;
import java.net.URL;

public class Managers {

    private Managers() {
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefaultTask(boolean isLoad) throws IOException {
        return new HttpTaskManager(new URL("http://localhost"), isLoad);
    }

    public static Gson getGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .create();
    }
}