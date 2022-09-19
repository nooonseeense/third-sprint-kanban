package service;

import tasks.Task;
import java.util.Comparator;

public class TasksIdComparator implements Comparator<Task> {

    @Override
    public int compare(Task o1, Task o2) {
        return Integer.compare(o1.getId(), o2.getId());
    }
}
