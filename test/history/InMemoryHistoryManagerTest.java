package history;

import managers.*;
import tasks.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest {

    public TaskManager testTaskManager;

    @BeforeEach
    void createNewTaskManager() {
        testTaskManager = Managers.getDefault();
    }

    //история умеет добавлять таски
    @Test
    void historyManagerAddsTasksCorrectly() {

        Task testTask1 = new Task("a", "a");
        testTaskManager.addNewTask(testTask1);
        testTaskManager.getTaskById(testTask1.getId());

        assertEquals(testTask1, testTaskManager.getHistory().get(0),
                "история просмотров не записала обращение к Task");
    }

    //история умеет удалять таски, если они удалены менеджером
    @Test
    void historyManagerClearTasksCorrectly() {

        Task testTask1 = new Task("a", "a");

        testTaskManager.addNewTask(testTask1);
        testTaskManager.getTaskById(testTask1.getId());
        testTaskManager.clearListOfTasks();

        assertEquals(0, testTaskManager.getHistory().size(),
                "история просмотров не удалила Task после того, как он был удалён из менеджера (clearListOfTasks)");

        testTaskManager.addNewTask(testTask1);
        testTaskManager.getTaskById(testTask1.getId());
        testTaskManager.clearTasksById(testTask1.getId());

        assertEquals(0, testTaskManager.getHistory().size(),
                "история просмотров не удалила Task после того, как он был удалён из менеджера (clearTasksById)");
    }

    //история умеет добавлять эпики
    @Test
    void historyManagerAddsEpicsCorrectly() {

        Epic testEpic = new Epic("a", "b");
        testTaskManager.addNewEpic(testEpic);
        testTaskManager.getEpicById(testEpic.getId());

        assertEquals(testEpic, testTaskManager.getHistory().get(0),
                "история просмотров не записала обращение к Epic");
    }

    //история умеет удалять эпики, если они удалены менеджером
    @Test
    void historyManagerClearEpicsCorrectly() {

        Epic testEpic = new Epic("a", "b");

        testTaskManager.addNewEpic(testEpic);
        testTaskManager.getEpicById(testEpic.getId());
        testTaskManager.clearListOfEpics();

        assertEquals(0, testTaskManager.getHistory().size(),
                "история просмотров не удалила Epic после того, как он был удалён из менеджера (clearListOfEpics)");

        testTaskManager.addNewEpic(testEpic);
        testTaskManager.getEpicById(testEpic.getId());
        testTaskManager.clearEpicsById(testEpic.getId());

        assertEquals(0, testTaskManager.getHistory().size(),
                "история просмотров не удалила Epic после того, как он был удалён из менеджера (clearEpicsById)");
    }

    //история умеет добавлять сабтаски
    @Test
    void historyManagerAddsSubsCorrectly() {

        Epic testEpic = new Epic("a", "b");
        testTaskManager.addNewEpic(testEpic);

        Subtask testSub = new Subtask("a", "b", testEpic.getId());
        testTaskManager.addNewSubtask(testSub);
        testTaskManager.getSubtaskById(testSub.getId());

        assertEquals(testSub, testTaskManager.getHistory().get(0),
                "история просмотров не записала обращение к Subtask");
    }

    //после удаления эпика из истории исчезают и все его подзадачи
    @Test
    void historyRemovesSubsWhenEpicIsRemoved() {

        Epic testEpic = new Epic("a", "b");
        testTaskManager.addNewEpic(testEpic);

        Subtask testSub = new Subtask("a", "b", testEpic.getId());
        testTaskManager.addNewSubtask(testSub);

        testTaskManager.getSubtaskById(testSub.getId());
        testTaskManager.clearSubtasksById(testSub.getId());

        assertEquals(0, testTaskManager.getHistory().size(),
                "история просмотров не удалила Subtask после того, как он был удалён из менеджера (clearSubtasksById)");

        testTaskManager.addNewSubtask(testSub);
        testTaskManager.getSubtaskById(testSub.getId());
        testTaskManager.clearListOfSubtasks();

        assertEquals(0, testTaskManager.getHistory().size(),
                "история просмотров не удалила Subtask после того, как он был удалён из менеджера (clearListOfSubtasks)");

        testTaskManager.addNewSubtask(testSub);
        testTaskManager.getSubtaskById(testSub.getId());
        testTaskManager.clearListOfEpics();

        assertEquals(0, testTaskManager.getHistory().size(),
                "история просмотров не удалила Subtask после того, как он был удалён из менеджера (clearListOfEpics)");
    }

    //история не дублирует записи при повторном просмотре, но оставляет последний
    @Test
    void historyContainsUniqueElementsAndShowsTheLastAccessing() {

        Task testTask = new Task("a", "a");
        testTaskManager.addNewTask(testTask);

        int i = 0;
        while (i < 12) {
            testTaskManager.getTaskById(testTask.getId());
            ++i;
        }

        assertEquals(1, testTaskManager.getHistory().size(),
                "история просмотров содержит более 1-го элемента после повторных обращений");

        Task testTaskDiff = new Task("b", "b");
        testTaskManager.addNewTask(testTaskDiff);
        testTaskManager.getTaskById(testTaskDiff.getId());

        assertEquals(testTaskDiff.getId(), testTaskManager.getHistory().get(1).getId(),
                "история просмотров не записала последний элемент");

        testTaskManager.getTaskById(testTask.getId());

        assertEquals(testTask.getId(), testTaskManager.getHistory().get(1).getId(),
                "история просмотров не перезаписала повторное обращение");
    }
}