public class Main {
    public static void main(String[] args) {
        Manager manager = new Manager();

        Task taskOne = new Task("Задача-1", "Описание-1");
        manager.addNewTask(taskOne);

        Epic epicOne = new Epic("Эпик-2", "Описание-2");
        manager.addNewEpic(epicOne);
        Subtask subtaskOne = new Subtask("Саб-3 для эпика-2", "Описание-3", epicOne);
        manager.addNewSubtask(subtaskOne);
        Subtask subtaskTwo = new Subtask("Саб-4 для эпика-2", "Описание-4", epicOne);
        manager.addNewSubtask(subtaskTwo);

        Epic epicTwo = new Epic("Эпик-5", "Описание-5");
        manager.addNewEpic(epicTwo);
        Subtask subtaskThree = new Subtask("Саб-6 для эпика-5", "Саб-6 для эпика-5", epicTwo);
        manager.addNewSubtask(subtaskThree);

        System.out.println("Созданы 1 задача, 2 эпика и 3 сабтаска: ");
        System.out.println(manager.getListOfTasks());
        System.out.println(manager.getListOfEpics());
        System.out.println(manager.getListOfSubtasks());
        System.out.println();

        Task updTask = new Task("Задача апд", "Описание апд");
        manager.updateTask(updTask, 1, "IN_PROGRESS");

        Subtask updSubtaskOne = new Subtask("Саб-3 для эпика-2", "Саб-3 для эпика-2", epicOne);
        manager.updateSubtask(updSubtaskOne, 3, "DONE");

        System.out.println("Обновлена задача и сабтаск для эпика-2: ");
        System.out.println(manager.getListOfTasks());
        System.out.println(manager.getListOfEpics());
        System.out.println(manager.getListOfSubtasks());
        System.out.println();

        Subtask updSubtaskTwo = new Subtask("Саб-4 для эпика-2", "Саб-4 для эпика-2", epicOne);
        manager.updateSubtask(updSubtaskTwo, 4, "DONE");

        System.out.println("Обновлён сабтаск эпика-2: ");
        System.out.println(manager.getListOfEpics());
        System.out.println(manager.getListOfSubtasks());
        System.out.println();

        manager.clearSubtasksById(4);
        System.out.println("Удалён сабтаск эпика-2: ");
        System.out.println(manager.getListOfSubtasks());
        System.out.println(manager.getSubtasksByEpic(epicOne));
        System.out.println();

        manager.clearEpicsById(2);
        System.out.println("Удалён один эпик: ");
        System.out.println(manager.getListOfEpics());
        System.out.println("Сабтаски удалённого эпика: " + manager.getSubtasksByEpic(epicOne));
        System.out.println("Сабтаски оставшегося эпика: " + manager.getSubtasksByEpic(epicTwo));
        manager.clearListOfEpics();
        System.out.println("Удалены все эпики: ");
        System.out.println(manager.getListOfEpics());
        System.out.println(manager.getListOfSubtasks());
    }
}