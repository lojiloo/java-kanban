package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedManagerTest {
    private static File tempFile;
    private static TaskManager testFileBackedTaskManager;

    @BeforeEach
    void createTempFile() {
        try {
            tempFile = File.createTempFile("tempFile", "txt");
            testFileBackedTaskManager = new FileBackedTaskManager(tempFile.getAbsolutePath());
        } catch (IOException e) {
            e.getStackTrace();
        }
    }

    //сохранение и загрузка пустого файла
    @Test
    void fileBackedTaskManagerCreatesFileCorrectly() {
        assertNotNull(tempFile, "переданный файл не был создан");
        assertNotNull(testFileBackedTaskManager, "менеджер не был инициализирован");
    }

    //сохранение задач в InMemoryTaskManager
    @Test
    void fileBackedTaskManagerSavesTasksInMemoryCorrectly() {
        Task task = new Task("task name", "task description");
        testFileBackedTaskManager.addNewTask(task);
        assertEquals(1, testFileBackedTaskManager.getListOfTasks().size(),
                "менеджер не сохранил задачу в оперативную память");

        Epic epic = new Epic("epic name", "epic description");
        testFileBackedTaskManager.addNewEpic(epic);
        assertEquals(1, testFileBackedTaskManager.getListOfEpics().size(),
                "менеджер не сохранил эпик в оперативную память");

        Subtask subtask = new Subtask("sub name", "sub description", 2);
        testFileBackedTaskManager.addNewSubtask(subtask);
        assertEquals(1, testFileBackedTaskManager.getListOfSubtasks().size(),
                "менеджер не сохранил эпик в оперативную память");
    }

    //сохранение задач в файл
    @Test
    void fileBackedTaskManagerSavesTasksInFileCorrectly() {
        try (BufferedReader reader = new BufferedReader(new FileReader(tempFile))) {
            List<String> tasksInFile = new ArrayList<>();

            reader.readLine();
            while (reader.ready()) {
                String inFile = reader.readLine();
                tasksInFile.add(inFile);
            }

            int totalInMemory = testFileBackedTaskManager.getListOfTasks().size()
                    + testFileBackedTaskManager.getListOfEpics().size()
                    + testFileBackedTaskManager.getListOfSubtasks().size();

            assertEquals(tasksInFile.size(), totalInMemory,
                    "количество задач в файле и в оперативной памяти не совпадает");
        } catch (IOException e) {
            e.getStackTrace();
        }
    }

    //добавление задач в историю просмотров
    @Test
    void fileBackedTaskManagerSavesTasksInHistoryCorrectly() {
        Task task = new Task("task name", "task description");
        testFileBackedTaskManager.addNewTask(task);
        assertEquals(0, testFileBackedTaskManager.getHistory().size(),
                "история просмотров не пуста, когда ни одна задача не была просмотрена");
        testFileBackedTaskManager.getTaskById(1);
        assertEquals(1, testFileBackedTaskManager.getHistory().size(),
                "история просмотров пуста после того, как была просмотрена задача");
    }

    //восстановление менеджера из файла
    @Test
    void managerFromFileEqualsToManagerInitializedAtRuntime() {
        Task task = new Task("task name", "task description");
        testFileBackedTaskManager.addNewTask(task);
        List<Task> tasks = testFileBackedTaskManager.getListOfTasks();

        Epic epic = new Epic("epic name", "epic description");
        testFileBackedTaskManager.addNewEpic(epic);
        List<Epic> epics = testFileBackedTaskManager.getListOfEpics();

        Subtask subtask = new Subtask("sub name", "sub description", 2);
        testFileBackedTaskManager.addNewSubtask(subtask);
        List<Subtask> subtasks = testFileBackedTaskManager.getListOfSubtasks();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        for (Task t : tasks) {
            assertEquals(t, loadedManager.getTaskById(t.getId()),
                    "задачи не совпадают");
        }
        for (Epic e : epics) {
            assertEquals(e, loadedManager.getEpicById(e.getId()),
                    "эпики не совпадают");
        }
        for (Subtask s : subtasks) {
            assertEquals(s, loadedManager.getSubtaskById(s.getId()),
                    "сабтаски не совпадают");
        }
    }

    //исключение ManagerSaveException в методе loadFromFile корректно перехватывается
    @Test
    void managerSaveExceptionInLoadFromFileMethodTest() {

        Exception e = assertThrows(ManagerSaveException.class, () -> {
            TaskManager test = FileBackedTaskManager.loadFromFile(Paths.get("this_file_does_not_exist.txt").toFile());
        }, "метод loadFromFile не выбрасывает исключение managerSaveException");

        String expectedMessage = "Ошибка менеджера: ошибка при сохранении в файл";
        String actualMessage = e.getMessage();

        assertEquals(expectedMessage, actualMessage,
                "Перехваченное исключение содержит неожиданное сообщение");
    }
}
