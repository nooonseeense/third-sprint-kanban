package manager;

abstract class TaskManagerTest<T extends TaskManager> {     // Здесь мы тестируем все общие методы
    protected T taskManager;

    public TaskManagerTest(T taskManager) {
        this.taskManager = taskManager;
    }

}
