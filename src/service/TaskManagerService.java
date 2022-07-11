package service;

import constants.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import java.util.HashMap;

public class TaskManagerService { // Сюда записываем все созданные задачи
    protected HashMap<Integer, Task> tasks = new HashMap<>(); // Здесь будет записан id и задачи
    protected HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();
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
    // 3.1 Получение списка всех задач мапы task
    public void getListTask(HashMap<Integer, Task> tasks) {

    }

    public void getListEpic(HashMap<Integer, Task> epics) {

    }
    // 3.2 Удаление всех задач в мапе task
    public void taskAllDelete(HashMap<Integer, Task> tasks) {

    }

    public void epicAllDelete(HashMap<Integer, Task> epics) {

    }

    public void subtaskAllDelete(HashMap<Integer, Task> subtasks) {

    }
    // 3.3 Получение по идентификатору
    public void getIdTask(HashMap<Integer, Task> tasks) {

    }

    public void getIdEpic(HashMap<Integer, Task> epics) {
    // Получение списка всех подзадач определённого эпика.
    }

    public void getIdSubtask(HashMap<Integer, Task> subtasks) {

    }
    // 3.4 Удаление задач по идентефикатороу
    public void deleteTaskInIds(HashMap<Integer, Task> tasks) {

    }

    public void deleteEpicInIds(HashMap<Integer, Task> epics) {

    }

    public void deleteSubTaskInIds(HashMap<Integer, Task> subtasks) {

    }
    // 3.5 Обновление статуса Епика
    public void updateEpicStatus(Epic epic) {
        if (epics.isEmpty()) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
        if (epics.containsValue(Status.DONE)
                && (!epics.containsValue(Status.NEW))
                || (!epics.containsValue(Status.IN_PROGRESS))) {
            epic.setStatus(Status.DONE);
        }
    }
}
