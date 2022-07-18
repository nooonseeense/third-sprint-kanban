package service;

import constants.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import java.util.*;

public class TaskManagerService { // Сюда записываем все созданные задачи
    private HashMap<Integer, Task> tasks = new HashMap<>(); // Здесь будет записан id и задачи
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();

    private int generator = 0;
//******************************* 1. ДОБАВЛЕНИЕ ЗАДАЧ(СОЗДАНИЕ) В HashMap<Integer, Task> С УНИКАЛЬНЫМ ID ***************
    public void addTask(Task task) {   // ЭТИ МЕТОДЫ БУДУТ СОДЕРЖАТЬ ОСНОВНУЮ ЛОГИКУ ПО РАБОТЕ С БД
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
        subtasks.put(subtaskId, subtask); // добавляем подзадачу в епик
        epic.getSubtaskIds().add(subtaskId); // добавляем ИД САБТАСКА
        updateEpicStatus(epic); // определяем статус эпика | ПРИ УДАЛЕНИИ ТАК ЖН ОБНОВЛЯЕМ СТАТУС ЕПИКА
    }
//********************************* 2. ОБНОВЛЕНИЕ ЗАДАЧ ****************************************************************
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void updateSubtask(Subtask subtask) { // [!] ДОДЕЛАТЬ ПОСЛЕ ИЗМЕНЕНИЯ МЕТОДА ОБНОВЛЕНИЯ СТАТУСА [!]
        subtasks.put(subtask.getId(), subtask);
        for (Epic value : epics.values()) {
            if (subtask.getEpicId() == value.getId()) {
                updateEpicStatus(value);
            }
        }
    }
//********************************* 3. МЕТОДЫ ЗАДАЧ ********************************************************************
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
    // 3.2 Удаление всех задач в мапе task
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
    // 3.3 Получение по идентификатору
    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }
    // 3.4 Удаление задач по идентефикатороу
    public void deleteTaskInIds(int id) {
        tasks.remove(id);
    }

    public void deleteEpicInIds(int id) {
        for (Epic value : epics.values()) { // идем по мапе c ЭПИКАМИ
            if (epics.containsKey(id)) { // попадаем в нужный ЭПИК по ID
                for (Subtask subtask : subtasks.values()) { // идем по мапе с САБТАСКАМИ
                    if (value.getId() == subtask.getEpicId()) { // ЕСЛИ ID Епика РАВЕН ЕПИК ID в сабтаске
                        subtasks.remove(subtask.getEpicId()); // тут неправильно)

                    }
                }
                value.getSubtaskIds().clear();
            }
        }
    }

    public void deleteSubTaskInIds(Epic epic, int id) { // [!] - НЕ ЗАБЫТЬ ПРО СМЕНУ СТАТУСА ЕПИКА
        ArrayList<Integer> subtaskIds = new ArrayList<>();
        for (Task value : subtasks.values()) {
            subtaskIds.add(value.getId());
        }
        if (subtaskIds.contains(id)) {
            subtasks.remove(id);
            updateEpicStatus(epic);
        }
    }
    // 3.5 Обновление статуса Епика
    private void updateEpicStatus(Epic epic) {
        if (subtasks.isEmpty()) {
           epic.setStatus(Status.NEW);
           return;
        } else {
            for (int idSubtask : epic.getSubtaskIds()) { // Попали в мапу с задачами и проходимся по ключам
                for (Subtask value : subtasks.values()) {
                    if (value.getId() == idSubtask) { // ищем по сапоставлению
                        if (value.getStatus().equals(Status.IN_PROGRESS)) {
                            epic.setStatus(Status.IN_PROGRESS);
                        } else if (value.getStatus().equals(Status.DONE)) {
                            epic.setStatus(Status.DONE);
                        } else {
                            epic.setStatus(Status.NEW);
                        }
                    }
                }
            }
        }
    }
}

// if (epics.isEmpty()) {
//         epic.setStatus(Status.NEW);
//         } else {
//         epic.setStatus(Status.IN_PROGRESS);
//         }
//         for (int key : subtasks.keySet()) {
//         if (subtasks.get(key).getEpicId() == epic.getId()
//         && Status.DONE.equals(subtasks.get(key).getStatus())) {
//         epic.setStatus(Status.DONE);
//         }
//         }
