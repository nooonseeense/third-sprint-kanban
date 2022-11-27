package manager;

import constants.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final Set<Task> sortedListTasksAndSubtasks = new TreeSet<>();
    private int generator = 0;

    public int getGenerator() {
        return generator;
    }

    public void setGenerator(int generator) {
        this.generator = generator;
    }

    @Override
    public void addTask(Task task) {
        int taskId = generator++;
        task.setId(taskId);

        if (searchForTheIntersectionOfTask(task)) {
            return;
        }
        tasks.put(taskId, task);
        sortedListTasksAndSubtasks.add(task);
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
        int subtaskId = generator++;
        subtask.setId(subtaskId);

        if (epic == null || searchForTheIntersectionOfTask(subtask)) {
            return;
        }
        subtasks.put(subtaskId, subtask);
        sortedListTasksAndSubtasks.add(subtask);
        epic.setSubtaskIds(subtaskId);
        updateEpicStatus(epic);

        if (!(subtask.getStartTime() == null)) {
            calculateStartAndEndTimeEpic(epics.get(epicId));
        }
    }

    @Override
    public void updateTask(Task task) {
        if (searchForTheIntersectionOfTask(task)) {
            return;
        }
        tasks.put(task.getId(), task);
        sortedListTasksAndSubtasks.add(task);
    }

    @Override
    public void updateEpic(Epic epic) {
        calculateStartAndEndTimeEpic(epic);
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (searchForTheIntersectionOfTask(subtask)) {
            return;
        }
        subtasks.put(subtask.getId(), subtask);
        sortedListTasksAndSubtasks.remove(subtask);
        sortedListTasksAndSubtasks.add(subtask);

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
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
            sortedListTasksAndSubtasks.remove(task);
        }
        tasks.clear();
    }

    @Override
    public void epicAllDelete() {
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
        }
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
            sortedListTasksAndSubtasks.remove(subtask);
        }
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void subtaskAllDelete() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
            sortedListTasksAndSubtasks.remove(subtask);
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
    public List<Integer> getEpicSubtasksList(int id) {
        return epics.get(id).getSubtaskIds();
    }

    @Override
    public void deleteTaskInIds(int id) {
        sortedListTasksAndSubtasks.remove(tasks.get(id));
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpicInIds(int id) {
        Epic epic = epics.get(id);
        for (int subtaskId : epic.getSubtaskIds()) {
            subtasks.remove(subtaskId);
            historyManager.remove(subtaskId);
            sortedListTasksAndSubtasks.remove(subtasks.get(subtaskId));
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
        sortedListTasksAndSubtasks.remove(subtask);
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
    public Set<Task> getPrioritizedTasks() {
        return new TreeSet<>(sortedListTasksAndSubtasks);
    }

    @Override
    public boolean searchForTheIntersectionOfTask(Task task) {
        for (Task value : sortedListTasksAndSubtasks) {
            if (value.getStartTime() == null || task.getStartTime() == null || value.getId() == task.getId()) {
                continue;
            }
            if ((value.getStartTime().isEqual(task.getEndTime()))
                    || value.getStartTime().isBefore(task.getEndTime())
                    && (value.getStartTime().isEqual(task.getStartTime())
                    || value.getEndTime().isAfter(task.getStartTime()))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void calculateStartAndEndTimeEpic(Epic epic) {
        int epicDuration = 0;

        if (epic.getSubtaskIds().size() == 0) {
            epic.setStartTime(null);
            epic.setEndTime(null);
            epic.setDuration(0);
            return;
        }

        for (Integer subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = subtasks.get(subtaskId);

            if (subtask.getStartTime() != null) {
                epic.setEndTime(subtask.getEndTime());
                epicDuration += subtask.getDuration();
                epic.setDuration(epicDuration);

                if (epic.getStartTime() != null) {
                    continue;
                } else {
                    epic.setStartTime(subtask.getStartTime());
                }
            }
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}