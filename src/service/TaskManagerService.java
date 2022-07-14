package service;

import constants.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class TaskManagerService { // Сюда записываем все созданные задачи
    private HashMap<Integer, Task> tasks = new HashMap<>(); // Здесь будет записан id и задачи
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();

    private int generator = 0;
//******************************* 1. ДОБАВЛЕНИЕ ЗАДАЧ(СОЗДАНИЕ) В HashMap<Integer, Task> С УНИКАЛЬНЫМ ID ***************
    // ЭТИ МЕТОДЫ БУДУТ СОДЕРЖАТЬ ОСНОВНУЮ ЛОГИКУ ПО РАБОТЕ С БД
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
        } else { // сохраянем подзадачу
            int subtaskId = generator++;
            subtask.setId(subtaskId);
            subtasks.put(subtaskId, subtask); // добавляем подзадачу в епик
            epic.getSubtaskIds().add(subtaskId); // добавляем ИД САБТАСКА
            updateEpicStatus(epic); // определяем статус эпика | ПРИ УДАЛЕНИИ ТАК ЖН ОБНОВЛЯЕМ СТАТУС ЕПИКА
        }
    }
//********************************* 2. ОБНОВЛЕНИЕ ЗАДАЧ ****************************************************************
// Новая версия объекта с верным идентификатором передаётся в виде параметра.
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
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

    // 3.2 Удаление всех задач в мапе task
    public void taskAllDelete() {
       tasks.clear();
    }

    public void epicAllDelete() {
        epics.clear();
    }

    public void subtaskAllDelete(Epic epic) {
        subtasks.clear();
        updateEpicStatus(epic);
    }
    // 3.3 Получение по идентификатору
    public void getTaskById(int id) {
        for (Task values : tasks.values()) {
            if (values.getId() == id) {
                System.out.println(tasks.get(id));
            }
        }
    }

    public void getEpicById(Epic epic) {
        int id = epic.getId();
        // Получение списка всех подзадач определённого эпика.
    }

    // 3.4 Удаление задач по идентефикатороу
    public void deleteTaskInIds() {

    }

    public void deleteEpicInIds() {

    }

    public void deleteSubTaskInIds() {

    }
    // 3.5 Обновление статуса Епика
    public void updateEpicStatus(Epic epic) {
        if (epics.isEmpty()) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
        for (int key : subtasks.keySet()) {
            if (subtasks.get(key).getEpicId() == epic.getId()
                    && Status.DONE.equals(subtasks.get(key).getStatus())) {
                epic.setStatus(Status.DONE);
            }
        }
    }
}
