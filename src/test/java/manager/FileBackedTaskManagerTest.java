package manager;

import java.io.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    // Здесь мы тестируем локальные методы FileBackedTaskManager, которые не используются в inMemoryTaskManager
    public FileBackedTaskManagerTest() {
        super(new FileBackedTaskManager(new File("src/main/java/data/data.csv")));
    }

    public void saveTest() {

    }

    public void loadFromFileTest() {

    }
}
