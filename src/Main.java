public class Main {
    public static void main(String[] args) {
        Manager manager = new Manager();

        Epic epic = new Epic("Написать нормальный код", "Не убиться в процессе");
        manager.addNewEpic(epic);
        System.out.println(manager.getListOfEpics());

        Subtask firstSubtask = new Subtask("Поплакать", "Не сильно", epic.id);
        manager.addNewSubtask(firstSubtask);
        System.out.println(manager.getListOfSubtasks());

        firstSubtask.status = "DONE";
        manager.updateSubtask(firstSubtask);
        System.out.println(manager.getListOfEpics());
        System.out.println(manager.getListOfSubtasks());

        manager.clearListOfSubtasks();
        System.out.println(manager.getListOfEpics());
        System.out.println(manager.getListOfSubtasks());

        Subtask secondSubtask = new Subtask("Заварить чаю", "И выдохнуть", 1);
        secondSubtask.status = "IN_PROGRESS";
        manager.addNewSubtask(secondSubtask);
        System.out.println(manager.getSubtasksByEpic(epic));

        manager.clearSubtasksById(3);
        System.out.println(manager.getListOfEpics());

        manager.clearListOfEpics();
        System.out.println(manager.getListOfEpics());
        System.out.println(manager.getListOfSubtasks());

        Task task = new Task("Лечь пораньше", "И наконец-то выспаться");
        manager.addNewTask(task);
        System.out.println(manager.getTaskById(4));
    }
}