package tasks;

import managers.FileBackedTaskManager;
import managers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubtaskTest {
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

    //сабтаск всегда знает, к какому эпику он относится
    @Test
    void subtaskHasAccurateInfoAboutItsEpicId() {
        Epic testEpic = new Epic("a", "b");
        testFileBackedTaskManager.addNewEpic(testEpic);
        Subtask testSub = new Subtask("c", "d", 1);
        testFileBackedTaskManager.addNewSubtask(testSub);

        assertEquals(testEpic.getId(), testSub.getEpicId(),
                "айди эпика не совпадает с тем, что указано в сабтаске");
        assertEquals(testFileBackedTaskManager.getEpicById(testEpic.getId()), testFileBackedTaskManager.getEpicById(testSub.getEpicId()),
                "сабтаск не указывает на свой эпик");
    }

}