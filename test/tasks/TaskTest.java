package tasks;

import managers.FileBackedTaskManager;
import managers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {
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

    //после обновления таска успешно меняются его имя, описание, статус и айди
    @Test
    void fieldsShouldBeUpdatedWhenTaskHasBeenUpdated() {
        Task testTask = new Task("a", "b");
        testFileBackedTaskManager.addNewTask(testTask);
        testTask.setName("name");
        testFileBackedTaskManager.updateTask(testTask);

        assertEquals("name", testTask.getName(),
                "после обновления таска его имя не изменилось");

        testTask.setDescription("description");
        testFileBackedTaskManager.updateTask(testTask);

        assertEquals("description", testTask.getDescription(),
                "после обновления таска его описание не изменилось");

        testTask.setId(100);
        testFileBackedTaskManager.updateTask(testTask);

        assertEquals(100, testTask.getId(),
                "после обновления таска его айди не изменился");


        testTask.setStatus(Status.DONE);
        testFileBackedTaskManager.updateTask(testTask);

        assertEquals(Status.DONE, testTask.getStatus(),
                "после обновления таска его статус не изменился");
    }

}