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

    //если все сабы имеют статус DONE, то и у эпика статус DONE
    @Test
    void shouldBeDoneWhenAllSubtasksAreDone() {

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