package manager;

import constants.Status;
import exceptions.SearchForTheIntersectionOfTaskException;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected HistoryManager historyManager = new InMemoryHistoryManager();
    protected T taskManager;

    public TaskManagerTest(T taskManager) {
        this.taskManager = taskManager;

    }

    @Test
    public void addNewTaskTest() {
        Task task = new Task("TASK", "TASK_DESCRIPTION", Status.NEW);
        taskManager.addTask(task);

        final Task savedTask = taskManager.getTasks().get(task.getId());
        final List<Task> tasks = taskManager.getTasks();

        assertEquals(task.getId(), savedTask.getId(), "Id tasks do not match.");
        assertNotNull(tasks, "Tasks do not return.");
        assertEquals(1, tasks.size(), "Wrong amount tasks.");
        assertEquals(task, tasks.get(0), "Object tasks do not match.");
    }

    @Test
    public void addNewEpicTest() {
        Epic epic = new Epic("EPIC", "EPIC_DESCRIPTION");
        taskManager.addEpic(epic);

        final Epic savedEpic = taskManager.getEpics().get(epic.getId());
        final List<Epic> epics = taskManager.getEpics();

        assertEquals(epic.getId(), savedEpic.getId(), "Id epics do not match.");
        assertNotNull(epics, "Epics do not return.");
        assertEquals(1, epics.size(), "Wrong amount epics.");
        assertEquals(epic, epics.get(0), "Object epics do not match.");
        assertEquals(Status.NEW, epic.getStatus(), "Incorrect epic status");
    }

    @Test
    public void addNewSubtaskTest() {
        Epic epic = new Epic("EPIC", "EPIC_DESCRIPTION");
        Subtask subtask = new Subtask(
                "SUBTASK1",
                "SUBTASK_DESCRIPTION",
                Status.NEW,
                60,
                LocalDateTime.of(2021, Month.JULY, 14, 10, 0),
                epic.getId()
        );
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask);

        final List<Subtask> subtasks = taskManager.getSubtasks();

        assertNotNull(subtasks, "Subtasks do not return.");
        assertEquals(1, subtasks.size(), "Wrong amount subtasks.");
        assertEquals(subtask, subtasks.get(0), "Object epics do not match.");
        assertEquals(epic.getSubtaskIds().get(0), subtask.getId(), "Epic has not subtask");
    }

    @Test
    public void updateTaskTest() {
        Task task = new Task("TASK", "TASK_DESCRIPTION", Status.NEW);
        taskManager.addTask(task);

        task.setStartTime(LocalDateTime.of(2022, Month.NOVEMBER, 2, 14, 30));
        taskManager.updateTask(task);
        assertEquals(task.getStartTime(), taskManager.getTasks().get(task.getId()).getStartTime());
    }

    @Test
    public void updateEpicTest() {
        Epic epic = new Epic("EPIC", "EPIC_DESCRIPTION");
        Subtask subtask = new Subtask(
                "SUBTASK1",
                "SUBTASK_DESCRIPTION",
                Status.NEW,
                60,
                LocalDateTime.of(2021, Month.JULY, 14, 10, 0),
                epic.getId()
        );
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask);

        epic.setStatus(Status.IN_PROGRESS);
        taskManager.updateEpic(epic);
        assertEquals(epic.getStatus(), taskManager.getEpics().get(epic.getId()).getStatus());
    }

    @Test
    public void updateSubtaskTest() {
        Epic epic = new Epic("EPIC", "EPIC_DESCRIPTION");
        Subtask subtask = new Subtask(
                "SUBTASK1",
                "SUBTASK_DESCRIPTION",
                Status.NEW,
                60,
                LocalDateTime.of(2021, Month.JULY, 14, 10, 0),
                epic.getId()
        );
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask);

        subtask.setStatus(Status.DONE);
        taskManager.updateSubtasks(subtask);

        assertEquals(subtask.getStatus(), taskManager.getSubtasks().get(0).getStatus());
        assertEquals(Status.DONE, taskManager.getEpics().get(0).getStatus());
        assertEquals(subtask.getStartTime(), epic.getStartTime(), "Start time does not match");
        assertEquals(subtask.getEndTime(), epic.getEndTime(), "End time does not match");
    }

    @Test
    public void getTasksTest() {
        Task task = new Task("TASK", "TASK_DESCRIPTION", Status.NEW);
        assertEquals(0, taskManager.getTasks().size(), "The list is not empty");
        taskManager.addTask(task);

        taskManager.deleteTaskInIds(task.getId());
        final IndexOutOfBoundsException exception = assertThrows(IndexOutOfBoundsException.class,
                () -> taskManager.getTasks().get(task.getId()));
        assertEquals("Index 0 out of bounds for length 0", exception.getMessage());

        taskManager.addTask(task);
        assertEquals(1, taskManager.getTasks().size(), "Wrong number of objects in sheet");
    }

    @Test
    public void getEpicsTest() {
        Epic epic = new Epic("EPIC", "EPIC_DESCRIPTION");
        assertEquals(0, taskManager.getEpics().size(), "The list is not empty");
        taskManager.addEpic(epic);

        taskManager.deleteEpicInIds(epic.getId());
        final IndexOutOfBoundsException exception = assertThrows(IndexOutOfBoundsException.class,
                () -> taskManager.getEpics().get(epic.getId()));
        assertEquals("Index 0 out of bounds for length 0", exception.getMessage());

        taskManager.addEpic(epic);
        assertEquals(1, taskManager.getEpics().size(), "Wrong number of objects in sheet");
    }

    @Test
    public void getSubtaskTest() {
        Epic epic = new Epic("EPIC", "EPIC_DESCRIPTION");
        Subtask subtask = new Subtask(
                "SUBTASK1",
                "SUBTASK_DESCRIPTION",
                Status.DONE,
                60,
                LocalDateTime.of(2021, Month.JULY, 14, 10, 0),
                epic.getId()
        );

        assertEquals(0, taskManager.getSubtasks().size(), "The list is not empty");

        final IndexOutOfBoundsException indexOutOfBoundsException = assertThrows(IndexOutOfBoundsException.class,
                () -> taskManager.getSubtasks().get(subtask.getId()));
        assertEquals("Index 0 out of bounds for length 0", indexOutOfBoundsException.getMessage());

        final NullPointerException nullPointerException = assertThrows(NullPointerException.class,
                () -> taskManager.deleteSubTaskInIds(subtask.getId()));
        assertNull(nullPointerException.getMessage());
    }

    @Test
    public void taskAllDeleteTest() {
        Task task = new Task("TASK", "TASK_DESCRIPTION", Status.NEW);

        taskManager.addTask(task);
        assertEquals(1, taskManager.getTasks().size(), "The task has not been added");

        taskManager.taskAllDelete();
        assertEquals(0, taskManager.getTasks().size(), "List is not empty");
    }

    @Test
    public void epicAllDeleteTest() {
        Epic epic = new Epic("EPIC", "EPIC_DESCRIPTION");

        taskManager.addEpic(epic);
        assertEquals(1, taskManager.getEpics().size(), "The epic has not been added");

        taskManager.epicAllDelete();
        assertEquals(0, taskManager.getEpics().size(), "List is not empty");
    }

    @Test
    public void subtaskAllDeleteTest() {
        Epic epic = new Epic("EPIC", "EPIC_DESCRIPTION");
        Subtask subtask = new Subtask(
                "SUBTASK1",
                "SUBTASK_DESCRIPTION",
                Status.NEW,
                60,
                LocalDateTime.of(2021, Month.JULY, 14, 10, 0),
                epic.getId()
        );
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask);

        assertEquals(1, taskManager.getSubtasks().size(), "The subtasks has not been added");
        assertEquals(subtask.getId(), epic.getSubtaskIds().get(0), "Subtask has no binding to epic");

        taskManager.epicAllDelete();
        assertEquals(0, taskManager.getSubtasks().size(), "List is not empty");
    }

    @Test
    public void getTaskByIdTest() {
        Task task = new Task("TASK", "TASK_DESCRIPTION", Status.NEW);
        taskManager.addTask(task);
        assertEquals(task, taskManager.getTaskById(task.getId()), "Object with this id does not exist");
    }

    @Test
    public void getEpicByIdTest() {
        Epic epic = new Epic("EPIC", "EPIC_DESCRIPTION");
        taskManager.addEpic(epic);
        assertEquals(epic, taskManager.getEpicById(epic.getId()), "Object with this id does not exist");
    }

    @Test
    public void getSubtaskByIdTest() {
        Epic epic = new Epic("EPIC", "EPIC_DESCRIPTION");
        Subtask subtask = new Subtask(
                "SUBTASK1",
                "SUBTASK_DESCRIPTION",
                Status.NEW,
                60,
                LocalDateTime.of(2021, Month.JULY, 14, 10, 0),
                epic.getId()
        );
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask);

        assertEquals(subtask, taskManager.getSubtaskById(subtask.getId()));
    }

    @Test
    public void deleteTaskInIdsTest() {
        Task task = new Task("TASK", "TASK_DESCRIPTION", Status.NEW);
        taskManager.addTask(task);
        taskManager.deleteTaskInIds(task.getId());

        assertEquals(0, taskManager.getTasks().size(), "Task has not been deleted");
        assertEquals(0, historyManager.getHistory().size(), "Task has not been deleted in history");
    }

    @Test
    public void deleteEpicInIdsTest() {
        Epic epic = new Epic("EPIC", "EPIC_DESCRIPTION");
        taskManager.addEpic(epic);
        taskManager.deleteEpicInIds(epic.getId());

        assertEquals(0, taskManager.getEpics().size(), "Task has not been deleted");
        assertEquals(0, historyManager.getHistory().size(), "Task has not been deleted in history");
    }

    @Test
    public void deleteSubTaskInIdsTest() {
        Epic epic = new Epic("EPIC", "EPIC_DESCRIPTION");
        Subtask subtask = new Subtask(
                "SUBTASK1",
                "SUBTASK_DESCRIPTION",
                Status.IN_PROGRESS,
                60,
                LocalDateTime.of(2021, Month.JULY, 14, 10, 0),
                epic.getId()
        );
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask);
        taskManager.deleteSubTaskInIds(subtask.getId());

        assertEquals(Status.NEW, epic.getStatus(), "Epic status is wrong");
        assertEquals(0, taskManager.getSubtasks().size(), "Task has not been deleted");
        assertEquals(0, historyManager.getHistory().size(), "Task has not been deleted in history");
    }

    @Test
    public void getPrioritizedTasksTest() {
        Task task1 = new Task("Task1", "Description1", Status.NEW, 60,
                LocalDateTime.of(2022, Month.DECEMBER, 2, 14, 30));
        Task task2 = new Task("Task2", "Description2", Status.NEW, 60,
                LocalDateTime.of(2022, Month.NOVEMBER, 2, 14, 30));
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        Set<Task> sortedTasks = taskManager.getPrioritizedTasks();
        List<Task> sortedTasksList = new LinkedList<>(sortedTasks);

        assertEquals(0, sortedTasksList.get(task2.getId()).getId(), "Objects in sheet are not sorted");
    }

    @Test
    public void getHistoryTest() {
        Task task1 = new Task("Task1", "Description1", Status.NEW, 60,
                LocalDateTime.of(2022, Month.DECEMBER, 2, 14, 30));
        taskManager.addTask(task1);
        taskManager.getTaskById(task1.getId());

        assertEquals(task1.getId(), taskManager.getHistory().get(task1.getId()).getId());
    }

    @Test
    public void calculateEpicDurationTest() {
        Epic epic = new Epic("EPIC", "EPIC_DESCRIPTION");
        Subtask subtask = new Subtask(
                "SUBTASK1",
                "SUBTASK_DESCRIPTION",
                Status.IN_PROGRESS,
                60,
                LocalDateTime.of(2021, Month.JULY, 14, 10, 0),
                epic.getId()
        );
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask);
        assertEquals(60, epic.getDuration(), "Неправильное время продолжительности эпика.");

        Subtask subtask2 = new Subtask(
                "SUBTASK1",
                "SUBTASK_DESCRIPTION",
                Status.IN_PROGRESS,
                550,
                LocalDateTime.of(2021, Month.NOVEMBER, 14, 10, 0),
                epic.getId()
        );
        assertThrows(SearchForTheIntersectionOfTaskException.class, () -> taskManager.addSubtask(subtask2));
        assertEquals(60, epic.getDuration(), "Неправильное время продолжительности эпика.");
    }

    @Test
    public void calculateStartAndEndTimeEpicTest() {
        Epic epic = new Epic("EPIC", "EPIC_DESCRIPTION");
        Subtask subtask = new Subtask(
                "SUBTASK1",
                "SUBTASK_DESCRIPTION",
                Status.IN_PROGRESS,
                60,
                LocalDateTime.of(2021, Month.JULY, 14, 10, 0),
                epic.getId()
        );
        Subtask subtask2 = new Subtask(
                "SUBTASK1",
                "SUBTASK_DESCRIPTION",
                Status.IN_PROGRESS,
                50,
                LocalDateTime.of(2021, Month.JULY, 14, 14, 0),
                epic.getId()
        );
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask);

        assertThrows(SearchForTheIntersectionOfTaskException.class, () -> taskManager.addSubtask(subtask2));
        assertEquals(LocalDateTime.parse("2021-07-14T10:00"), epic.getStartTime());
        assertEquals(LocalDateTime.parse("2021-07-14T11:00"), epic.getEndTime());

        taskManager.subtaskAllDelete();
        taskManager.updateEpic(epic);

        assertNull(epic.getStartTime());
        assertNull(epic.getEndTime());
        assertEquals(0, epic.getDuration());
    }

    @Test
    public void shouldReturnStatusNewForEmptyEpic() {
        Epic epic = new Epic("EPIC", "EPIC_DESCRIPTION");
        taskManager.addEpic(epic);
        assertEquals(0, epic.getSubtaskIds().size(), "Wrong epic subtasks size");
        assertEquals(Status.NEW, epic.getStatus(), "Invalid epic status");
    }

    @Test
    public void shouldReturnEpicWithNewStatus() {
        Epic epic = new Epic("EPIC", "EPIC_DESCRIPTION");
        Subtask subtask = new Subtask(
                "SUBTASK1",
                "SUBTASK_DESCRIPTION",
                Status.NEW,
                60,
                LocalDateTime.of(2021, Month.JULY, 14, 10, 0),
                epic.getId()
        );
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask);
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    public void shouldReturnEpicWithDoneStatus() {
        Epic epic = new Epic("EPIC", "EPIC_DESCRIPTION");
        Subtask subtask = new Subtask(
                "SUBTASK1",
                "SUBTASK_DESCRIPTION",
                Status.DONE,
                60,
                LocalDateTime.of(2021, Month.JULY, 14, 10, 0),
                epic.getId()
        );
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask);
        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    public void shouldReturnEpicWithInProgressStatus() {
        Epic epic = new Epic("EPIC", "EPIC_DESCRIPTION");
        Subtask subtask = new Subtask(
                "SUBTASK",
                "SUBTASK_DESCRIPTION",
                Status.NEW,
                60,
                LocalDateTime.of(2021, Month.JULY, 14, 10, 0),
                epic.getId()
        );
        Subtask subtask2 = new Subtask(
                "SUBTASK2",
                "SUBTASK_DESCRIPTION2",
                Status.DONE,
                70,
                LocalDateTime.of(2022, Month.JULY, 14, 10, 0),
                epic.getId()
        );
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask);
        assertThrows(SearchForTheIntersectionOfTaskException.class, () -> taskManager.addSubtask(subtask2));
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    public void shouldReturnEpicStatusInProgress() {
        Epic epic = new Epic("EPIC", "EPIC_DESCRIPTION");
        Subtask subtask = new Subtask(
                "SUBTASK",
                "SUBTASK_DESCRIPTION",
                Status.IN_PROGRESS,
                60,
                LocalDateTime.of(2021, Month.JULY, 14, 10, 0),
                epic.getId()
        );
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask);
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }
}