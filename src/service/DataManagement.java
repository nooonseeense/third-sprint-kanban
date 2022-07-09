package service;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class DataManagement {
    protected HashMap<Integer, Task> tasks = new HashMap<>(); // Здесь будет записан id и задачи
    protected HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    protected ArrayList<Integer> subtaskIds = new ArrayList<>();
}
