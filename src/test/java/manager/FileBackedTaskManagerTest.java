package manager;

import org.junit.jupiter.api.BeforeEach;
import java.io.File;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    // Здесь мы тестируем локальные методы FileBackedTaskManager, которые не используются в inMemoryTaskManager
    public FileBackedTaskManagerTest(FileBackedTaskManager fileBackedTaskManager) {
        super(fileBackedTaskManager);
    }

    @BeforeEach
    public void beforeEach() {
        taskManager = new FileBackedTaskManager(new File("src/main/java/data/data.csv"));
    }

}
