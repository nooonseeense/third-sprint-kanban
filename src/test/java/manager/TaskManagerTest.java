package manager;

import constants.Status;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {    // Здесь мы тестируем все общие методы
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

        final List<Subtask> subtasks = taskManager.getSubtask();

        assertNotNull(subtasks, "Subtasks do not return.");
        assertEquals(1, subtasks.size(), "Wrong amount subtasks.");
        assertEquals(subtask, subtasks.get(0), "Object epics do not match.");
        assertEquals(epic.getSubtaskIds().get(0), subtask.getId(), "Epic has not subtask");
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
        taskManager.addSubtask(subtask2);
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void shouldReturnEpicStatusInProgressWithInProgressSubtaskStatus() {
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
