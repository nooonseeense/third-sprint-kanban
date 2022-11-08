package manager;

public class Managers {

    private Managers() {}

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TasksManager getDefaultTask() {
        return new InMemoryTasksManager();
    }
}