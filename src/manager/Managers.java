package manager;

public class Managers {
    private static final TaskManager TASK_MANAGER = new InMemoryTaskManager();

    private Managers() {}

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefaultTask() {
        return TASK_MANAGER;
    }
}
