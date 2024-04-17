import managers.FileBackedTaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        FileBackedTaskManager manager = new FileBackedTaskManager("file.txt");
        Task task1 = new Task("task1 name", "task1 description");
        //task1.setId(1234);
        manager.addNewTask(task1);
        Task task2 = new Task("task2 name", "task2 description");
        manager.addNewTask(task2);
        Epic epic = new Epic("epic name", "epic description");
        //epic.setId(22);
        manager.addNewEpic(epic);

        FileBackedTaskManager manager2 = FileBackedTaskManager.loadFromFile(new File("file.txt"));
        Epic epic1 = new Epic("epic1 name", "epic1 description");
        //epic1.setId(346856);
        manager2.addNewEpic(epic1);
        Subtask sub1 = new Subtask("sub1 name", "sub1 description", manager2.getListOfEpics().get(0).getId());
        manager2.addNewSubtask(sub1);
        System.out.println(manager2.getListOfEpics());
    }
}
