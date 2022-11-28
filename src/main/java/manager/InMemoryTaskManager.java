package manager;

import constants.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDateTime;
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
        int subtaskId = generator++;
        subtask.setId(subtaskId);
        Epic epic = epics.get(epicId);

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
        sortedListTasksAndSubtasks.remove(tasks.get(task.getId()));
        tasks.put(task.getId(), task);
        sortedListTasksAndSubtasks.add(task);
    }

    @Override
    public void updateEpic(Epic epic) {
        calculateStartAndEndTimeEpic(epic);
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateSubtasks(Subtask subtask) {
        if (searchForTheIntersectionOfTask(subtask)) {
            return;
        }
        sortedListTasksAndSubtasks.remove(subtasks.get(subtask.getId()));
        subtasks.put(subtask.getId(), subtask);
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
            sortedListTasksAndSubtasks.remove(subtasks.get(subtaskId));
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
        sortedListTasksAndSubtasks.remove(subtask);
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
    public Set<Task> getPrioritizedTasks() {
        return new TreeSet<>(sortedListTasksAndSubtasks);
    }

    @Override
    public boolean searchForTheIntersectionOfTask(Task task) {
        if (task.getStartTime() == null) {
            return false;
        }
        for (Task value : sortedListTasksAndSubtasks) {
            if (value.getStartTime() == null || value.getId() == task.getId()) {
                continue;
            }
            if (task.getStartTime().isAfter(value.getEndTime()) && task.getEndTime().isBefore(value.getStartTime())) {
                continue;
            } else {
                return true;// добавить исключение
            }
//            if (task.getStartTime().isBefore(value.getEndTime()) && task.getStartTime().isAfter(value.getStartTime())) {
//                return true;
//            }
//            if (task.getEndTime().isBefore(value.getEndTime()) && task.getStartTime().isAfter(value.getStartTime())) {
//                return true;
//            }
//            if (task.getStartTime().isBefore(value.getStartTime()) && task.getEndTime().isAfter(value.getStartTime())) {
//                return true;
//            }
//            if (!task.getEndTime().isAfter(value.getStartTime())) {
//                continue;
//            }
//            if (!task.getEndTime().isBefore(value.getStartTime())) {
//                continue;
//            }
        }
        return false;
    }

    @Override
    public void calculateStartAndEndTimeEpic(Epic epic) {
        LocalDateTime startTimeEpic = null;
        LocalDateTime endTimeEpic = null;
        int duration = 0;

        if (epic.getSubtaskIds().size() == 0) {
            epic.setStartTime(null);
            epic.setEndTime(null);
            epic.setDuration(0);
            return;
        }

        for (int subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = subtasks.get(subtaskId);

            if (subtask.getStartTime() != null) {
                if (startTimeEpic == null || subtask.getStartTime().isBefore(startTimeEpic)) {
                    startTimeEpic = subtask.getStartTime();
                }
                if (subtask.getStartTime().isAfter(startTimeEpic)) {
                    endTimeEpic = subtask.getEndTime();
                }
                duration += subtask.getDuration();
            }
        }
        epic.setStartTime(startTimeEpic);
        epic.setEndTime(endTimeEpic);
        epic.setDuration(duration);
    }

    @Override
    public List<Task> getAllTasks() {
        List<Task> allTasks = new LinkedList<>();
        allTasks.addAll(getTasks());
        allTasks.addAll(getEpics());
        allTasks.addAll(getSubtask());
        return allTasks;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}