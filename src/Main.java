import constants.Status;
import manager.HistoryManager;
import manager.InMemoryTaskManager;
import manager.Managers;
import service.PrintConsole;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        HistoryManager historyManager = Managers.getHistoryManagerMemory(); // утилитарный класс

        Task task1 = new Task("Task 1", "Описание", Status.NEW);
        Task task2 = new Task("Task 2", "Описание", Status.IN_PROGRESS);

        inMemoryTaskManager.addTask(task1);
        inMemoryTaskManager.addTask(task2);

        Epic epic1 = new Epic("Построить дом", "Купить бетон");
        Epic epic2 = new Epic("Сходить в магазин", "Купить хлеб");
        Epic epic4 = new Epic("Откопать сокровище", "Особо ценное");

        inMemoryTaskManager.addEpic(epic1);
        inMemoryTaskManager.addEpic(epic2);
        inMemoryTaskManager.addEpic(epic4);

        Subtask subtask1 = new Subtask("Заполнить бетон", "Оп", Status.DONE, epic1.getId());
        Subtask subtask2 = new Subtask("Купить шампунь", "Оп_2", Status.IN_PROGRESS, epic2.getId());
        Subtask subtask4 = new Subtask("Оборвать веревку", "()", Status.IN_PROGRESS, epic4.getId());

        inMemoryTaskManager.updateSubtask(subtask4);
        inMemoryTaskManager.addSubTask(subtask1);
        inMemoryTaskManager.addSubTask(subtask2);

        PrintConsole printConsole = new PrintConsole();

        List<Task> tasks = inMemoryTaskManager.getTasks();
        List<Epic> epics = inMemoryTaskManager.getEpics();
        List<Subtask> subtasks = inMemoryTaskManager.getSubtask();

        printConsole.printTasks(tasks);
        printConsole.printEpics(epics);
        printConsole.printSubTasksInEpic(epic4, subtasks);

        inMemoryTaskManager.getTaskById(1);
        inMemoryTaskManager.getEpicById(2);
        inMemoryTaskManager.getSubtaskById(4);

        Task task3 = new Task("Task 3 (ОБНОВЛЕННЫЙ)", "(Описание)", Status.NEW);
        Task task4 = new Task("Task 4 (ОБНОВЛЕННЫЙ_2)", "(Описание_2)", Status.NEW);

        task3.setId(task2.getId());
        inMemoryTaskManager.updateTask(task3);
        inMemoryTaskManager.updateTask(task4);

        tasks.add(task3);
        tasks.add(task4);

        inMemoryTaskManager.deleteSubTaskInIds(5);
        printConsole.printSubTasksInEpic(epic1, subtasks);
        inMemoryTaskManager.deleteEpicInIds(2);

    }
}
