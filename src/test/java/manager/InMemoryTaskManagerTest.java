package manager;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    // Здесь мы тестируем локальные методы inMemoryTaskManager, которые не используются в FileBackedTaskManager
    public InMemoryTaskManagerTest() {
        super(new InMemoryTaskManager());
    }

}
