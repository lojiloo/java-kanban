import managers.FileBackedTaskManager;
import managers.Managers;
import managers.TaskManager;
import tasks.Task;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = new FileBackedTaskManager("file.txt");
        Task task1 = new Task("t1", "t1");
        Task task2 = new Task("t2", "t2");
        manager.addNewTask(task1);
        manager.addNewTask(task2);

        manager.setTemporal(task1, 2024, 5, 7, 23, 30, 60);
        manager.setTemporal(task2, 2024, 5, 7, 21, 0, 60);

        System.out.println(manager.getPrioritizedTasks());
    }
}
