package managers;

import tasks.*;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    //менеджер создаёт проинициализированные экземпляры, готовые к работе
    @Test
    void getReadyForWorkDefaultManager() {
        TaskManager testTaskManager = Managers.getDefault();

        assertNotNull(testTaskManager, "таскменеджер не инициализирован");
        assertNotNull(testTaskManager.getHistory(), "история просмотров не инициализирована");

        Task testTask = new Task("a","a");
        testTaskManager.addNewTask(testTask);
        assertEquals(1, testTaskManager.getListOfTasks().size(), "нет задач после добавления задачи");

        testTaskManager.getTaskById(1);
        assertEquals(1, testTaskManager.getHistory().size(), "задача не сохранена в историю");

        testTaskManager.clearListOfTasks();
        assertEquals(0, testTaskManager.getListOfTasks().size(), "список задач не обнулён");
    }

    //история просмотров содержит ровно 10 последних тасков
    @Test
    void shouldContainTheLastTenTasks() {
        TaskManager testTaskManager = Managers.getDefault();
        Task testTask = new Task("a", "a");
        testTaskManager.addNewTask(testTask);

        int i = 0;
        while (i < 12) {
            testTaskManager.getTaskById(1);
            ++i;
        }

        assertEquals(10, testTaskManager.getHistory().size(),
                "история просмотров содержит более 10 элементов");

        Task testTaskDifferent = new Task("b", "b");
        testTaskManager.addNewTask(testTaskDifferent);
        testTaskManager.getTaskById(testTaskDifferent.getId());

        assertEquals(testTaskDifferent.getId(), testTaskManager.getHistory().get(9).getId(),
                "история просмотров не записала последний элемент");
    }

    //менеджер умеет обавлять задачи разных типов и умеет находить их по ID
    @Test
    void InMemoryTaskManagerWorksWithAnyTypeOfTasks() {
        TaskManager testTaskManager = Managers.getDefault();

        Task testTask = new Task("a", "b");
        testTaskManager.addNewTask(testTask);

        Epic testEpic = new Epic("c", "d");
        testTaskManager.addNewEpic(testEpic);

        Subtask testSub = new Subtask("e", "f", testEpic.getId());
        testTaskManager.addNewSubtask(testSub);

        assertEquals(testTask, testTaskManager.getTaskById(testTask.getId()),
                "менеджер не сработал с задачей");
        assertEquals(testEpic, testTaskManager.getEpicById(testEpic.getId()),
                "менеджер не сработал с эпиком");
        assertEquals(testSub, testTaskManager.getSubtaskById(testSub.getId()),
                "менеджер не сработал с сабтаском");
        assertEquals(testSub, testTaskManager.getSubtasksByEpic(testEpic).get(0),
                "менеджер не записал информацию об эпике в сабтаск");
    }

    //таски с заданным и сгенерированным ID не конфликтуют между собой
    @Test
    void thereIsNoConflictWhenSettedIdAndAutoIdAreUsedBoth() {
        TaskManager testTaskManager = Managers.getDefault();

        Task testTaskID1 = new Task("a", "b");
        testTaskManager.addNewTask(testTaskID1);
        Task testTaskID2 = new Task("c", "d");
        testTaskManager.addNewTask(testTaskID2);

        testTaskManager.setId(testTaskID1, 2);
        testTaskManager.updateTask(testTaskID1);

        assertNotEquals(testTaskID1.getId(), testTaskID2.getId(),
                "сгенерированный ID не поменял своего значения в пользу введённого вручную");
    }

    //задача остаётся неизменной по всем полям после добавления в менеджер
    @Test
    void taskStaysTheSameWhenIsAddedToManager() {
        TaskManager testTaskManager = Managers.getDefault();

        Task testTask = new Task("a", "b");
        String taskName = testTask.getName();
        String taskDescription = testTask.getDescription();
        Status taskStatus = testTask.getStatus();

        testTaskManager.addNewTask(testTask);

        assertEquals(taskName, testTaskManager.getTaskById(testTask.getId()).getName(),
                "таск изменил значение переменной name после добавления в менеджер");
        assertEquals(taskDescription, testTaskManager.getTaskById(testTask.getId()).getDescription(),
                "таск изменил значение переменной description после добавления в менеджер");
        assertEquals(taskStatus, testTaskManager.getTaskById(testTask.getId()).getStatus(),
                "таск изменил значение переменной status после добавления в менеджер");
    }

    //задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных
    @Test
    void historyManagerSavesLastVersionOfTask() {
        TaskManager testTaskManager = Managers.getDefault();

        Task testTask = new Task("a", "b");
        testTaskManager.addNewTask(testTask);
        testTaskManager.getTaskById(1);

        testTask.setName("name");
        testTaskManager.updateTask(testTask);
        testTaskManager.getTaskById(1);

        assertNotEquals(testTaskManager.getHistory().get(0), testTaskManager.getHistory().get(1));
    }

}