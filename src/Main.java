import managers.FileBackedTaskManager;
import tasks.Subtask;
import tasks.Task;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        /*FileBackedTaskManager manager = new FileBackedTaskManager("file.txt");
        Epic epic1 = new Epic("epic1 name", "epic1 description");
        manager.addNewEpic(epic1);
        Subtask sub1 = new Subtask("sub1 name", "sub1 description", epic1.getId());
        manager.addNewSubtask(sub1);*/

        FileBackedTaskManager manager2 = FileBackedTaskManager.loadFromFile(new File("file.txt"));
        Task task1 = new Task("task1 name", "task1 description");
        manager2.addNewTask(task1);
        Subtask sub2 = new Subtask("sub2 name", "sub2 description", manager2.getListOfEpics().get(0).getId());
        manager2.addNewSubtask(sub2);
    }
}
