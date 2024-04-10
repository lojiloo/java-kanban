package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
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

    //новый менеджер создаёт проинициализированные экземпляры, готовые к работе
    @Test
    void getReadyForWorkDefaultManager() {
        assertNotNull(testFileBackedTaskManager, "таскменеджер не инициализирован");
        assertNotNull(testFileBackedTaskManager.getHistory(), "история просмотров не инициализирована");

        Task testTask = new Task("a", "a");
        testFileBackedTaskManager.addNewTask(testTask);
        assertEquals(1, testFileBackedTaskManager.getListOfTasks().size(), "нет задач после добавления задачи");

        testFileBackedTaskManager.getTaskById(1);
        assertEquals(1, testFileBackedTaskManager.getHistory().size(), "задача не сохранена в историю");

        testFileBackedTaskManager.clearListOfTasks();
        assertEquals(0, testFileBackedTaskManager.getListOfTasks().size(), "список задач не обнулён");
        assertEquals(0, testFileBackedTaskManager.getHistory().size(), "история не обнулена");
    }

    //новый менеджер умеет обновлять задачи разных типов и умеет находить их по ID
    @Test
    void InMemoryTaskManagerWorksWithAnyTypeOfTasks() {
        Task testTask = new Task("a", "b");
        testFileBackedTaskManager.addNewTask(testTask);

        Epic testEpic = new Epic("c", "d");
        testFileBackedTaskManager.addNewEpic(testEpic);

        Subtask testSub = new Subtask("e", "f", testEpic.getId());
        testFileBackedTaskManager.addNewSubtask(testSub);

        assertEquals(testTask, testFileBackedTaskManager.getTaskById(testTask.getId()),
                "менеджер не сработал с задачей");
        assertEquals(testEpic, testFileBackedTaskManager.getEpicById(testEpic.getId()),
                "менеджер не сработал с эпиком");
        assertEquals(testSub, testFileBackedTaskManager.getSubtaskById(testSub.getId()),
                "менеджер не сработал с сабтаском");
        assertEquals(testSub, testFileBackedTaskManager.getSubtasksByEpic(testEpic).get(0),
                "менеджер не записал информацию об эпике в сабтаск");
    }

    //таски с заданным и сгенерированным ID не конфликтуют между собой
    @Test
    void thereIsNoConflictWhenSettedIdAndAutoIdAreUsedBoth() {
        Task testTaskID1 = new Task("a", "b");
        testFileBackedTaskManager.addNewTask(testTaskID1);
        Task testTaskID2 = new Task("c", "d");
        testFileBackedTaskManager.addNewTask(testTaskID2);

        testFileBackedTaskManager.setId(testTaskID1, 2);
        testFileBackedTaskManager.updateTask(testTaskID1);

        assertNotEquals(testTaskID1.getId(), testTaskID2.getId(),
                "сгенерированный ID не поменял своего значения в пользу введённого вручную");
    }

    //задача остаётся неизменной по всем полям после добавления в менеджер
    @Test
    void taskStaysTheSameWhenIsAddedToManager() {
        Task testTask = new Task("a", "b");
        String taskName = testTask.getName();
        String taskDescription = testTask.getDescription();
        Status taskStatus = testTask.getStatus();

        testFileBackedTaskManager.addNewTask(testTask);

        assertEquals(taskName, testFileBackedTaskManager.getTaskById(testTask.getId()).getName(),
                "таск изменил значение переменной name после добавления в менеджер");
        assertEquals(taskDescription, testFileBackedTaskManager.getTaskById(testTask.getId()).getDescription(),
                "таск изменил значение переменной description после добавления в менеджер");
        assertEquals(taskStatus, testFileBackedTaskManager.getTaskById(testTask.getId()).getStatus(),
                "таск изменил значение переменной status после добавления в менеджер");
    }

}