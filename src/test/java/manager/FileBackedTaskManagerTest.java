package manager;

import java.io.File;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    // Здесь мы тестируем локальные методы FileBackedTaskManager, которые не используются в inMemoryTaskManager
    public FileBackedTaskManagerTest() {
        super(new FileBackedTaskManager(new File("src/main/java/data/data.csv")));
    }

}
