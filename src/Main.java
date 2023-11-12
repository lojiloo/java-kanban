public class Main {
    public static void main(String[] args) {
        Manager manager = new Manager();

        Task task = new Task("Простая задача", "Тест");
        manager.createNewTask(task);

        Epic epicOne = new Epic("Эпик 1", "Тест первый");
        manager.createNewEpic(epicOne);
        manager.createNewSubtask(epicOne,"Сабтаск 1", "Тест первый");
        manager.createNewSubtask(epicOne,"Сабтаск 2", "Тест первый");

        Epic epicTwo = new Epic("Эпик 2", "Тест второй");
        manager.createNewEpic(epicTwo);
        manager.createNewSubtask(epicTwo, "Сабтаск 1", "Тест второй");

        System.out.println(manager.showListOfTasksAndEpics());

        manager.updateTask(task);
        manager.updateEpic("Эпик 1", "Сабтаск 1");
        System.out.println(manager.getById(2));
        manager.updateEpic("Эпик 2", "Сабтаск 1");
        System.out.println(manager.getById(3));
        manager.updateEpic("Эпик 2", "Сабтаск 1");
        System.out.println(manager.getById(3));
        System.out.println(manager.getById(4));

        manager.clearListsById(1);
        System.out.println(manager.showListOfTasksAndEpics());
        manager.clearListsById(2);
        System.out.println(manager.showListOfTasksAndEpics());
        manager.clearLists();
        System.out.println(manager.showListOfTasksAndEpics());
    }
}