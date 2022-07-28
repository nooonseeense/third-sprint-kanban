package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import java.util.List;

public interface TaskManager {

    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubTask(Subtask subtask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    List<Epic> getEpics();

    List<Task> getTasks();

    List<Subtask> getSubtask();

    void taskAllDelete();

    void epicAllDelete();

    void subtaskAllDelete();

    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    void deleteTaskInIds(int id);

    void deleteEpicInIds(int id);

    void deleteSubTaskInIds(int id);

    void updateEpicStatus(Epic epic);
}