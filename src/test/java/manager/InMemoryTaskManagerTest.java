package manager;

import org.junit.jupiter.api.BeforeEach;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    // Здесь мы тестируем локальные методы inMemoryTaskManager, которые не используются в FileBackedTaskManager
    public InMemoryTaskManagerTest(InMemoryTaskManager inMemoryTaskManager) {
        super(inMemoryTaskManager);
    }

    @BeforeEach
    public void beforeEach() {
         taskManager = new InMemoryTaskManager();
    }
}
