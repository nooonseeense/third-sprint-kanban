package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;
import java.util.Set;

interface TaskManager {
    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubtask(Subtask subtask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtasks(Subtask subtask);

    List<Epic> getEpics();

    List<Task> getTasks();

    List<Subtask> getSubtasks();

    void taskAllDelete();

    void epicAllDelete();

    void subtaskAllDelete();

    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    List<Integer> getEpicSubtasksList(int id);

    void deleteTaskInIds(int id);

    void deleteEpicInIds(int id);

    void deleteSubTaskInIds(int id);

    void updateEpicStatus(Epic epic);

    Set<Task> getPrioritizedTasks();

    boolean searchForTheIntersectionOfTask(Task task);

    void calculateStartAndEndTimeEpic(Epic epic);

    List<Task> getAllTasks();

    List<Task> getHistory();
}