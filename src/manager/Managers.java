package manager;

public class Managers { // Утилитарный класс

    private static final TaskManager taskManager = new InMemoryTaskManager();

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefaultTask() {
        return taskManager;
    }
}
