package manager;

import tasks.Task;
import java.util.List;

public interface HistoryManager {

    void add(Task task); // добавить таску в лист историй

    List<Task> getTasks(); // получить таски из листа
}
