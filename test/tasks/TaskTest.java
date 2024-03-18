package tasks;

import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {
    static private TaskManager testManager = Managers.getDefault();

    //после обновления таска успешно меняются его имя, описание, статус и айди
    @Test
    void fieldsShouldBeUpdatedWhenTaskHasBeenUpdated() {
        Task testTask = new Task("a", "b");
        testManager.addNewTask(testTask);
        testTask.setName("name");
        testManager.updateTask(testTask);

        assertEquals("name", testTask.getName(),
                "после обновления таска его имя не изменилось");

        testTask.setDescription("description");
        testManager.updateTask(testTask);

        assertEquals("description", testTask.getDescription(),
                "после обновления таска его описание не изменилось");

        testTask.setId(100);
        testManager.updateTask(testTask);

        assertEquals(100, testTask.getId(),
                "после обновления таска его айди не изменился");


        testTask.setStatus(Status.DONE);
        testManager.updateTask(testTask);

        assertEquals(Status.DONE, testTask.getStatus(),
                "после обновления таска его статус не изменился");
    }

}