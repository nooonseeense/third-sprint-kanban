package manager;

import constants.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import java.util.*;

public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();

    private int generator = 0;

    public void addTask(Task task) {
        int taskId = generator++;
        task.setId(taskId);
        tasks.put(taskId, task);
    }

    public void addEpic(Epic epic) {
        int epicId = generator++;
        epic.setId(epicId);
        epics.put(epicId, epic);
    }

    public void addSubTask(Subtask subtask) {
        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }
        int subtaskId = generator++;
        subtask.setId(subtaskId);
        subtasks.put(subtaskId, subtask);
        epic.setSubtaskIds(subtaskId);
        updateEpicStatus(epic);

        System.out.println(epic.getSubtaskIds());
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        updateEpicStatus(epic);
    }

    public List<Epic> getEpics() {
        Collection<Epic> values = epics.values();
        return new ArrayList<>(values);
    }

    public List<Task> getTasks() {
        Collection<Task> values = tasks.values();
        return new ArrayList<>(values);
    }

    public List<Subtask> getSubtask() {
        Collection<Subtask> values = subtasks.values();
        return new ArrayList<>(values);
    }

    public void taskAllDelete() {
       tasks.clear();
    }

    public void epicAllDelete() {
        epics.clear();
        subtasks.clear();
    }

    public void subtaskAllDelete() {
        subtasks.clear();
        for (Epic value : epics.values()) {
            value.getSubtaskIds().clear();
            updateEpicStatus(value);
        }
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public void deleteTaskInIds(int id) {
        tasks.remove(id);
    }

    public void deleteEpicInIds(int id) {
        Epic epic = epics.get(id);
        for (int subtaskId : epic.getSubtaskIds()) {
            subtasks.remove(subtaskId);
        }
        epics.remove(id);
    }

    public void deleteSubTaskInIds(int id) {
        Subtask subtask = subtasks.get(id);
        Epic epic = getEpicById(subtask.getEpicId());
        epic.getSubtaskIds().remove((Integer) subtask.getId());
        subtasks.remove(id);
        updateEpicStatus(epic);
    }

    private void updateEpicStatus(Epic epic) {
        if (subtasks.isEmpty()) {
            epic.setStatus(Status.NEW);
        } else {
            ArrayList<Subtask> subtasksNew = new ArrayList<>();
            int counterDone = 0;
            int counterNew = 0;

            for (int i = 0; i < epic.getSubtaskIds().size(); i++) {
                subtasksNew.add(subtasks.get(epic.getSubtaskIds().get(i)));
            }
            if (!subtasks.isEmpty()) {
                epic.setStatus(Status.NEW);
                return;
            }
            for (Subtask value : subtasksNew) {
                switch (value.getStatus()) {
                    case NEW -> counterNew++;
                    case IN_PROGRESS -> {
                        epic.setStatus(Status.IN_PROGRESS);
                        return;
                    }
                    case DONE -> counterDone++;
                }
            }
            if (counterDone == subtasksNew.size()) {
                epic.setStatus(Status.DONE);
            } else if (counterNew == subtasksNew.size()) {
                epic.setStatus(Status.NEW);
            } else {
                epic.setStatus(Status.IN_PROGRESS);
            }
        }
    }
}