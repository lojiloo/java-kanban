import tasks.*;
import managers.*;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        Task task1 = new Task("первая задача", "тест 1");
        Task task2 = new Task("вторая задача", "тест 2");
        manager.addNewTask(task1);
        manager.addNewTask(task2);

        Epic epic1 = new Epic("эпик с подзадачами", "тест 3");
        manager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask("первая подзадача", "тест 4", 3);
        Subtask subtask2 = new Subtask("вторая подзадача", "тест 5", 3);
        Subtask subtask3 = new Subtask("третья подзадача", "тест 6", 3);
        manager.addNewSubtask(subtask1);
        manager.addNewSubtask(subtask2);
        manager.addNewSubtask(subtask3);

        Epic epic2 = new Epic("эпик без подзадач", "тест 7");
        manager.addNewEpic(epic2);

        manager.getTaskById(1);
        System.out.println(manager.getHistory());
        manager.getTaskById(1);
        System.out.println(manager.getHistory());
        manager.getTaskById(2);
        manager.getEpicById(3);
        System.out.println(manager.getHistory());
        manager.getTaskById(2);
        manager.getTaskById(1);
        System.out.println(manager.getHistory());
        manager.getSubtaskById(4);
        manager.getSubtaskById(6);
        System.out.println(manager.getHistory());
        manager.clearEpicsById(3);
        System.out.println(manager.getHistory());
        manager.clearListOfTasks();
        System.out.println(manager.getHistory());
    }
}