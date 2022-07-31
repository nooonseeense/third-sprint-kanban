package manager;

public class Managers {
    private static final TaskManager TASK_MANAGER = new InMemoryTaskManager();
    private static final HistoryManager HISTORY_MANAGER = new InMemoryHistoryManager();

    public static HistoryManager getDefaultHistory() {
        return HISTORY_MANAGER;
    }

    public static TaskManager getDefaultTask() {
        return TASK_MANAGER;
    }
}
