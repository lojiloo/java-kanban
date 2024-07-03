package tasks;

import managers.FileBackedTaskManager;
import managers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class EpicTest {
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

    //если айди эпиков совпадают, то и эпики совпадают
    @Test
    void epicShouldBeEqualWithTheSameID() {

        Epic testEpic = new Epic("a", "b");
        testFileBackedTaskManager.addNewEpic(testEpic);
        int testId = testEpic.getId();
        Epic equalEpic = testFileBackedTaskManager.getEpicById(testId);

        assertEquals(testEpic, equalEpic, "тест не пройден: эпики с одинаковым айди не равны");
    }

    //если у всех сабтасков статус NEW, то и у эпика статус NEW
    @Test
    void epicShouldBeNewWhenAllItsSubtasksAreNew() {
        Epic testEpic = new Epic("a", "b");
        testFileBackedTaskManager.addNewEpic(testEpic);

        Subtask testSubNew1 = new Subtask("a", "b", testEpic.getId());
        Subtask testSubNew2 = new Subtask("c", "d", testEpic.getId());
        testFileBackedTaskManager.addNewSubtask(testSubNew1);
        testFileBackedTaskManager.addNewSubtask(testSubNew2);

        assertEquals(Status.NEW, testSubNew1.getStatus(), "новый сабтаск имеет статус, отличный от NEW");
        assertEquals(Status.NEW, testEpic.getStatus(), "эпик имеет статус, отличный от NEW, когда все сабтаски NEW");
    }

    //если хотя бы у одного сабтаска статус не NEW, то эпик также не может быть NEW
    @Test
    void epicShouldChangeStatusWhenAtLeastOneItsSubtaskChangesIt() {

        Epic testEpic = new Epic("a", "b");
        testFileBackedTaskManager.addNewEpic(testEpic);

        Subtask testSubNew = new Subtask("a", "b", testEpic.getId());
        Subtask testSubInProgress = new Subtask("c", "d", testEpic.getId());
        testFileBackedTaskManager.addNewSubtask(testSubNew);
        testFileBackedTaskManager.addNewSubtask(testSubInProgress);

        assertEquals(Status.NEW, testEpic.getStatus(), "новый эпик имеет статус, отличный от NEW");

        testSubInProgress.setStatus(Status.IN_PROGRESS);
        testFileBackedTaskManager.updateSubtask(testSubInProgress);

        assertEquals(Status.IN_PROGRESS, testEpic.getStatus(),
                "эпик не изменил статус NEW после обновления сабтаска");
    }

    //если все сабы имеют статус IN_PROGRESS, то и у эпика статус IN_PROGRESS
    @Test
    void epicShouldBeInProgressWhenAllItsSubtasksAreInProgress() {
        Epic testEpic = new Epic("a", "b");
        testFileBackedTaskManager.addNewEpic(testEpic);

        Subtask testSubNew1 = new Subtask("a", "b", testEpic.getId());
        Subtask testSubNew2 = new Subtask("c", "d", testEpic.getId());
        testFileBackedTaskManager.addNewSubtask(testSubNew1);
        testFileBackedTaskManager.addNewSubtask(testSubNew2);

        testSubNew1.setStatus(Status.IN_PROGRESS);
        testFileBackedTaskManager.updateSubtask(testSubNew1);
        testSubNew2.setStatus(Status.IN_PROGRESS);
        testFileBackedTaskManager.updateSubtask(testSubNew2);

        assertEquals(Status.IN_PROGRESS, testEpic.getStatus(),
                "эпик имеет статус, отличный от IN_PROGRESS, когда все сабтаски IN_PROGRESS");
    }

    //если все сабы имеют статус DONE, то и у эпика статус DONE
    @Test
    void epicShouldBeDoneWhenAllItsSubtasksAreDone() {

        Epic testEpic = new Epic("a", "b");
        testFileBackedTaskManager.addNewEpic(testEpic);

        Subtask testSubDoneOne = new Subtask("a", "b", testEpic.getId());
        Subtask testSubDoneTwo = new Subtask("c", "d", testEpic.getId());
        testFileBackedTaskManager.addNewSubtask(testSubDoneOne);
        testFileBackedTaskManager.addNewSubtask(testSubDoneTwo);

        testSubDoneOne.setStatus(Status.DONE);
        testFileBackedTaskManager.updateSubtask(testSubDoneOne);
        testSubDoneTwo.setStatus(Status.DONE);
        testFileBackedTaskManager.updateSubtask(testSubDoneTwo);

        assertEquals(Status.DONE, testEpic.getStatus(), "эпик не завершён после завершения всех сабтасков");
    }

    //после удаления эпика удаляются все его сабтаски
    @Test
    void listOfSubtasksShouldBeEmptyWhenEpicIsDeleted() {

        testFileBackedTaskManager.clearListOfEpics();

        assertEquals(0, testFileBackedTaskManager.getListOfSubtasks().size(),
                "после удаления всех эпиков в менеджере остались сабтаски");

        Epic testEpic = new Epic("a", "b");
        testFileBackedTaskManager.addNewEpic(testEpic);
        Subtask testSub = new Subtask("a", "b", testEpic.getId());
        testFileBackedTaskManager.addNewSubtask(testSub);

        assertEquals(1, testFileBackedTaskManager.getSubtasksByEpic(testEpic).size(),
                "сабтаск не был сохранён в список сабтасков для данного эпика");

        testFileBackedTaskManager.clearEpicsById(testEpic.getId());

        assertNull(testFileBackedTaskManager.getSubtaskById(testSub.getId()),
                "после удаления эпика не были удалены его сабтаски");
    }

}