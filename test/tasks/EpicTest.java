package tasks;

import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {

    static private TaskManager testManager = Managers.getDefault();

    //если айди эпиков совпадают, то и эпики совпадают
    @Test
    void epicShouldBeEqualWithTheSameID() {

        Epic testEpic = new Epic("a", "b");
        testManager.addNewEpic(testEpic);
        int testId = testEpic.getId();
        Epic equalEpic = testManager.getEpicById(testId);

        assertEquals(testEpic, equalEpic, "тест не пройден: эпики с одинаковым айди не равны");
    }

    //если хотя бы у одного сабтаска статус не NEW, то эпик также не может быть NEW
    @Test
    void epicShouldChangeStatusWhenAtLeastOneItsSubtaskChangesIt() {

        Epic testEpic = new Epic("a", "b");
        testManager.addNewEpic(testEpic);

        Subtask testSubNew = new Subtask("a", "b", testEpic.getId());
        Subtask testSubInProgress = new Subtask("c", "d", testEpic.getId());
        testManager.addNewSubtask(testSubNew);
        testManager.addNewSubtask(testSubInProgress);

        assertEquals(Status.NEW, testEpic.getStatus(), "новый эпик имеет статус, отличный от NEW");

        testSubInProgress.setStatus(Status.IN_PROGRESS);
        testManager.updateSubtask(testSubInProgress);

        assertEquals(Status.IN_PROGRESS, testEpic.getStatus(),
                "эпик не изменил статус NEW после обновления сабтаска");
    }

    //если все сабы имеют статус DONE, то и у эпика статус DONE
    @Test
    void shouldBeDoneWhenAllSubtasksAreDone() {

        Epic testEpic = new Epic("a", "b");
        testManager.addNewEpic(testEpic);

        Subtask testSubDoneOne = new Subtask("a", "b", testEpic.getId());
        Subtask testSubDoneTwo = new Subtask("c", "d", testEpic.getId());
        testManager.addNewSubtask(testSubDoneOne);
        testManager.addNewSubtask(testSubDoneTwo);

        testSubDoneOne.setStatus(Status.DONE);
        testManager.updateSubtask(testSubDoneOne);
        testSubDoneTwo.setStatus(Status.DONE);
        testManager.updateSubtask(testSubDoneTwo);

        assertEquals(Status.DONE, testEpic.getStatus(), "эпик не завершён после завершения всех сабтасков");
    }

    //после удаления эпика удаляются все его сабтаски
    @Test
    void listOfSubtasksShouldBeEmptyWhenEpicIsDeleted() {

        testManager.clearListOfEpics();

        assertEquals(0, testManager.getListOfSubtasks().size(),
                "после удаления всех эпиков в менеджере остались сабтаски");

        Epic testEpic = new Epic("a", "b");
        testManager.addNewEpic(testEpic);
        Subtask testSub = new Subtask("a", "b", testEpic.getId());
        testManager.addNewSubtask(testSub);

        assertEquals(1, testManager.getSubtasksByEpic(testEpic).size(),
                "сабтаск не был сохранён в список сабтасков для данного эпика");

        testManager.clearEpicsById(testEpic.getId());

        assertNull(testManager.getSubtaskById(testSub.getId()),
                "после удаления эпика не были удалены его сабтаски");
    }

}