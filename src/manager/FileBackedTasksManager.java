package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

public class FileBackedTasksManager extends InMemoryTasksManager implements TasksManager {
    String path;

    public FileBackedTasksManager(String path) {
        this.path = path;
    }

    /**
     * Сохраняет текущее состояние менеджера в указанный файл
     */
    private void save() {

    }

    /**
     * Сначала выполняется версия, унаследованная от предка, а затем метод save()
     */
    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubTask(Subtask subtask) {
        super.addSubTask(subtask);
        save();
    }
}
