package tasks;

import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    static private TaskManager testManager = Managers.getDefault();

    //сабтаск всегда знает, к какому эпику он относится
    @Test
    void subtaskHasAccurateInfoAboutItsEpicId() {
        Epic testEpic = new Epic("a", "b");
        testManager.addNewEpic(testEpic);
        Subtask testSub = new Subtask("c", "d", 1);
        testManager.addNewSubtask(testSub);

        assertEquals(testEpic.getId(), testSub.getEpicId(),
                "айди эпика не совпадает с тем, что указано в сабтаске");
        assertEquals(testManager.getEpicById(testEpic.getId()), testManager.getEpicById(testSub.getEpicId()),
                "сабтаск не указывает на свой эпик");
    }

}