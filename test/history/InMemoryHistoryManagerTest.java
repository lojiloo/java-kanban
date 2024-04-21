package history;

import managers.FileBackedTaskManager;
import managers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest {

    private File tempFile;
    private TaskManager testFileBackedTaskManager;

    @BeforeEach
    void createTempFile() {
        try {
            tempFile = File.createTempFile("tempFile", "txt");
            testFileBackedTaskManager = new FileBackedTaskManager(tempFile.getAbsolutePath());
        } catch (IOException e) {
            e.getStackTrace();
        }
    }

    //история умеет добавлять таски
    @Test
    void historyManagerAddsTasksCorrectly() {
        Task testTask1 = new Task("a", "a");
        testFileBackedTaskManager.addNewTask(testTask1);
        testFileBackedTaskManager.getTaskById(testTask1.getId());

        assertEquals(testTask1, testFileBackedTaskManager.getHistory().get(0),
                "история просмотров не записала обращение к Task");
    }

    //история умеет удалять таски, если они удалены менеджером
    @Test
    void historyManagerClearTasksCorrectly() {
        Task testTask1 = new Task("a", "a");

        testFileBackedTaskManager.addNewTask(testTask1);
        testFileBackedTaskManager.getTaskById(testTask1.getId());
        testFileBackedTaskManager.clearListOfTasks();

        assertEquals(0, testFileBackedTaskManager.getHistory().size(),
                "история просмотров не удалила Task после того, как он был удалён из менеджера (clearListOfTasks)");

        Task testTask2 = new Task("a", "a");
        testFileBackedTaskManager.addNewTask(testTask2);
        testFileBackedTaskManager.getTaskById(testTask2.getId());
        testFileBackedTaskManager.clearTasksById(testTask2.getId());

        assertEquals(0, testFileBackedTaskManager.getHistory().size(),
                "история просмотров не удалила Task после того, как он был удалён из менеджера (clearTasksById)");
    }

    //история умеет добавлять эпики
    @Test
    void historyManagerAddsEpicsCorrectly() {
        Epic testEpic = new Epic("a", "b");
        testFileBackedTaskManager.addNewEpic(testEpic);
        testFileBackedTaskManager.getEpicById(testEpic.getId());

        assertEquals(testEpic, testFileBackedTaskManager.getHistory().get(0),
                "история просмотров не записала обращение к Epic");
    }

    //история умеет удалять эпики, если они удалены менеджером
    @Test
    void historyManagerClearEpicsCorrectly() {
        Epic testEpic = new Epic("a", "b");

        testFileBackedTaskManager.addNewEpic(testEpic);
        testFileBackedTaskManager.getEpicById(testEpic.getId());
        testFileBackedTaskManager.clearListOfEpics();

        assertEquals(0, testFileBackedTaskManager.getHistory().size(),
                "история просмотров не удалила Epic после того, как он был удалён из менеджера (clearListOfEpics)");

        Epic testEpic2 = new Epic("a", "b");
        testFileBackedTaskManager.addNewEpic(testEpic2);
        testFileBackedTaskManager.getEpicById(testEpic2.getId());
        testFileBackedTaskManager.clearEpicsById(testEpic2.getId());

        assertEquals(0, testFileBackedTaskManager.getHistory().size(),
                "история просмотров не удалила Epic после того, как он был удалён из менеджера (clearEpicsById)");
    }

    //история умеет добавлять сабтаски
    @Test
    void historyManagerAddsSubsCorrectly() {
        Epic testEpic = new Epic("a", "b");
        testFileBackedTaskManager.addNewEpic(testEpic);

        Subtask testSub = new Subtask("a", "b", testEpic.getId());
        testFileBackedTaskManager.addNewSubtask(testSub);
        testFileBackedTaskManager.getSubtaskById(testSub.getId());

        assertEquals(testSub, testFileBackedTaskManager.getHistory().get(0),
                "история просмотров не записала обращение к Subtask");
    }

    //после удаления эпика из истории исчезают и все его подзадачи
    @Test
    void historyRemovesSubsWhenEpicIsRemoved() {
        Epic testEpic = new Epic("a", "b");
        testFileBackedTaskManager.addNewEpic(testEpic);

        Subtask testSub = new Subtask("a", "b", testEpic.getId());
        testFileBackedTaskManager.addNewSubtask(testSub);

        testFileBackedTaskManager.getSubtaskById(testSub.getId());
        testFileBackedTaskManager.clearSubtasksById(testSub.getId());

        assertEquals(0, testFileBackedTaskManager.getHistory().size(),
                "история просмотров не удалила Subtask после того, как он был удалён из менеджера (clearSubtasksById)");

        Subtask testSub2 = new Subtask("a", "b", testEpic.getId());
        testFileBackedTaskManager.addNewSubtask(testSub2);
        testFileBackedTaskManager.getSubtaskById(testSub2.getId());
        testFileBackedTaskManager.clearListOfSubtasks();

        assertEquals(0, testFileBackedTaskManager.getHistory().size(),
                "история просмотров не удалила Subtask после того, как он был удалён из менеджера (clearListOfSubtasks)");

        Subtask testSub3 = new Subtask("a", "b", testEpic.getId());
        testFileBackedTaskManager.addNewSubtask(testSub3);
        testFileBackedTaskManager.getSubtaskById(testSub3.getId());
        testFileBackedTaskManager.clearListOfEpics();

        assertEquals(0, testFileBackedTaskManager.getHistory().size(),
                "история просмотров не удалила Subtask после того, как он был удалён из менеджера (clearListOfEpics)");
    }

    //история не дублирует записи при повторном просмотре, но оставляет последний
    @Test
    void historyContainsUniqueElementsAndShowsTheLastAccessing() {
        Task testTask = new Task("a", "a");
        testFileBackedTaskManager.addNewTask(testTask);

        int i = 0;
        while (i < 12) {
            testFileBackedTaskManager.getTaskById(testTask.getId());
            ++i;
        }

        assertEquals(1, testFileBackedTaskManager.getHistory().size(),
                "история просмотров содержит более 1-го элемента после повторных обращений");

        Task testTaskDiff = new Task("b", "b");
        testFileBackedTaskManager.addNewTask(testTaskDiff);
        testFileBackedTaskManager.getTaskById(testTaskDiff.getId());

        assertEquals(testTaskDiff.getId(), testFileBackedTaskManager.getHistory().get(1).getId(),
                "история просмотров не записала последний элемент");

        testFileBackedTaskManager.getTaskById(testTask.getId());

        assertEquals(testTask.getId(), testFileBackedTaskManager.getHistory().get(1).getId(),
                "история просмотров не перезаписала повторное обращение");
    }
}