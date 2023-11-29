public class Main {
    public static void main(String[] args) {
        InMemoryTaskManager manager = Managers.getDefault();

        Epic epic_1 = new Epic("Первый эпик", "Тест"); //1
        manager.addNewEpic(epic_1);
        Epic epic_2 = new Epic("Второй эпик", "Тест"); //2
        manager.addNewEpic(epic_2);
        Subtask subtask_11 = new Subtask("Первый саб первого эпика", "Тест", 1); //3
        manager.addNewSubtask(subtask_11);
        Subtask subtask_12 = new Subtask("Первый саб второго эпика", "Тест", 2); //4
        manager.addNewSubtask(subtask_12);
        Subtask subtask_22 = new Subtask("Второй саб второго эпика", "Тест", 2); //5
        manager.addNewSubtask(subtask_22);
        Subtask subtask_32 = new Subtask("Третий саб второго эпика", "Тест", 2); //6
        manager.addNewSubtask(subtask_32);
        Task task_1 = new Task("Первая задача", "Тест"); //7
        manager.addNewTask(task_1);
        Task task_2 = new Task("Вторая задача", "Тест"); //8
        manager.addNewTask(task_2);
        Task task_3 = new Task("Третья задача", "Тест"); //9
        manager.addNewTask(task_3);

        manager.getEpicById(1);
        manager.getEpicById(2);
        manager.getSubtaskById(3);
        manager.getSubtaskById(4);
        manager.getSubtaskById(5);
        manager.getSubtaskById(6);
        manager.getTaskById(7);
        manager.getTaskById(8);
        manager.getTaskById(9);
        manager.getTaskById(9);
        manager.getTaskById(9);
        manager.getTaskById(9);
        manager.getTaskById(9);

        System.out.println(manager.history.getHistory());

        subtask_22.status = Status.DONE;
        manager.updateSubtask(subtask_22);
        System.out.println(manager.getListOfEpics());
        System.out.println(manager.getSubtaskById(5));
    }
}