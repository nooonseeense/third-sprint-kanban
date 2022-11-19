package manager;

import constants.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected int generator = 0;

    @Override
    public void addTask(Task task) {
        int taskId = generator++;
        task.setId(taskId);
        tasks.put(taskId, task);
    }

    @Override
    public void addEpic(Epic epic) {
        int epicId = generator++;
        epic.setId(epicId);
        epics.put(epicId, epic);
    }

    @Override
    public void addSubtask(Subtask subtask) {
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
        calculateStartAndEndTimeEpic(epics.get(epicId));
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        updateEpicStatus(epic);
        calculateStartAndEndTimeEpic(epic);
    }

    @Override
    public List<Epic> getEpics() {
        Collection<Epic> values = epics.values();
        return new ArrayList<>(values);
    }

    @Override
    public List<Task> getTasks() {
        Collection<Task> values = tasks.values();
        return new ArrayList<>(values);
    }

    @Override
    public List<Subtask> getSubtask() {
        Collection<Subtask> values = subtasks.values();
        return new ArrayList<>(values);
    }

    @Override
    public void taskAllDelete() {
        for (Task value : tasks.values()) {
            historyManager.remove(value.getId());
        }
        tasks.clear();
    }

    @Override
    public void epicAllDelete() {
        for (Epic value : epics.values()) {
            historyManager.remove(value.getId());
        }
        for (Subtask value : subtasks.values()) {
            historyManager.remove(value.getId());
        }
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void subtaskAllDelete() {
        for (Subtask value : subtasks.values()) {
            historyManager.remove(value.getId());
        }
        subtasks.clear();

        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            updateEpicStatus(epic);
            calculateStartAndEndTimeEpic(epic);
        }
    }

    @Override
    public Task getTaskById(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public void deleteTaskInIds(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpicInIds(int id) {
        Epic epic = epics.get(id);
        for (int subtaskId : epic.getSubtaskIds()) {
            subtasks.remove(subtaskId);
            historyManager.remove(subtaskId);
        }
        epics.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteSubTaskInIds(int id) {
        Subtask subtask = subtasks.get(id);
        Epic epic = epics.get((subtask.getEpicId()));
        epic.getSubtaskIds().remove((Integer) subtask.getId());
        subtasks.remove(id);
        updateEpicStatus(epic);
        historyManager.remove(id);
        calculateStartAndEndTimeEpic(epic);
    }

    @Override
    public void updateEpicStatus(Epic epic) {
        if (subtasks.isEmpty()) {
            epic.setStatus(Status.NEW);
        } else {
            ArrayList<Subtask> subtasksNew = new ArrayList<>();
            int counterDone = 0;
            int counterNew = 0;

            for (int i = 0; i < epic.getSubtaskIds().size(); i++) {
                subtasksNew.add(subtasks.get(epic.getSubtaskIds().get(i)));
            }
            for (Subtask value : subtasksNew) {
                switch (value.getStatus()) {
                    case NEW:
                        counterNew++;
                        break;
                    case IN_PROGRESS:
                        epic.setStatus(Status.IN_PROGRESS);
                        return;
                    case DONE:
                        counterDone++;
                        break;
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

    @Override
    public void calculateEpicDuration(Epic epic, List<Subtask> newSubtasks) {
        int epicDuration = 0;

        for (Subtask subtask : newSubtasks) {
            epicDuration += subtask.getDuration();
        }
        epic.setDuration(epicDuration);
    }

    @Override
    public void calculateStartAndEndTimeEpic(Epic epic) {
        List<Subtask> allSubtasks = getSubtask();
        List<Subtask> newSubtasks = new LinkedList<>();

        if (allSubtasks.isEmpty()) {
            deleteEpicInIds(epic.getId());
            return;
        }

        for (Integer subtaskId : epic.getSubtaskIds()) {
            for (Subtask subtask : allSubtasks) {
                if (subtaskId == subtask.getId()) {
                    newSubtasks.add(subtask);
                }
            }
        }
        epic.setStartTime(newSubtasks.get(0).getStartTime());
        epic.setEndTime(newSubtasks.get(newSubtasks.size() - 1).getEndTime());
        calculateEpicDuration(epic, newSubtasks);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}