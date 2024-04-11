import managers.FileBackedTaskManager;
import tasks.Epic;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        /*FileBackedTaskManager manager = new FileBackedTaskManager("file.txt");

        Epic epic = new Epic("epic name", "epic description");
        manager.addNewEpic(epic);
        manager.getEpicById(epic.getId());
        System.out.println(manager.getListOfEpics());*/

        FileBackedTaskManager manager2 = FileBackedTaskManager.loadFromFile(new File("file.txt"));
        Epic epic2 = new Epic("epic2 name", "epic2 description");
        manager2.addNewEpic(epic2);
        System.out.println(manager2.getListOfEpics());
        System.out.println(manager2.getHistory());
    }
}
