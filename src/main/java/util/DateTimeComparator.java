package util;

import tasks.Task;

import java.util.Comparator;

public class DateTimeComparator implements Comparator<Task> {

    @Override
    public int compare(Task o1, Task o2) {
        return o1.getStartTime().compareTo(o2.getStartTime());
    }
}
