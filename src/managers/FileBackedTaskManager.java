package managers;

import history.HistoryManager;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final String path;
    protected TaskManager inMemoryTaskManager;

    public FileBackedTaskManager(String path) {
        this.path = path;
        inMemoryTaskManager = Managers.getDefault();
    }

    public static void main(String[] args) {
        FileBackedTaskManager manager = new FileBackedTaskManager("file.txt");

        Task task = new Task("task name", "task description");
        manager.addNewTask(task);

        Epic epic = new Epic("epic name", "epic description");
        manager.addNewEpic(epic);
        epic.setStatus(Status.DONE);
        manager.updateEpic(epic);

        manager.getTaskById(1);

        Subtask subtask = new Subtask("subtask name", "subtask description", 2);
        manager.addNewSubtask(subtask);
        manager.getSubtaskById(3);

        FileBackedTaskManager manager2 = loadFromFile(new File("file.txt"));

        System.out.println(manager2.getListOfTasks());
        System.out.println(manager2.getSubtasksByEpic(manager2.getEpicById(2)));

        Epic epic2 = new Epic("epic2 name", "epic2 description");
        manager2.addNewEpic(epic2);
        manager2.addNewEpic(epic2);
        System.out.println(manager2.getListOfEpics());
        manager2.getEpicById(4);
        System.out.println(manager2.getHistory());
    }

    public void save() throws ManagerSaveException {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path))) {

            //собираю задачи в файл
            List<Task> currentTasks = getListOfTasks();
            currentTasks.addAll(getListOfEpics());
            currentTasks.addAll(getListOfSubtasks());

            StringBuilder builder = new StringBuilder();
            bufferedWriter.write("id,type,name,status,description,epic\n");
            for (Task task : currentTasks) {
                builder.append(task.toString());
            }
            bufferedWriter.write(builder.toString());

            //записываю историю в этот же файл
            if (!super.getHistory().isEmpty()) {
                String currentHistory = historyToString(super.history);
                bufferedWriter.write(currentHistory);
            }

        } catch (IOException e) {
            throw new ManagerSaveException();
        }
    }

    static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file.getAbsolutePath());

        try {
            String content = Files.readString(Path.of(file.getAbsolutePath()));
            String[] lines = content.split("\n");

            List<Task> memory = new ArrayList<>();
            int lineCounter = 1;
            for (int i = 1; i < lines.length; i++) {
                if (!lines[i].equals("id,type,name,status,description,epic")) {
                    Task task = manager.fromString(lines[i]);
                    memory.add(task);
                    ++lineCounter;
                } else {
                    break;
                }
            }

            for (Task task : memory) {
                switch (task.getType()) {
                    case EPIC:
                        manager.addNewEpic((Epic) task);
                        break;
                    case SUBTASK:
                        manager.addNewSubtask((Subtask) task);
                        break;
                    default:
                        manager.addNewTask(task);
                }
            }


            if (lines.length > lineCounter) {
                StringBuilder history = new StringBuilder();
                for (int j = lineCounter + 1; j < lines.length; j++) {
                    history.append(lines[j]);
                }
                List<Integer> idInHistory = historyFromString(history.toString());

                for (int id : idInHistory) {
                    TaskType type = memory.get(id).getType();

                    switch (type) {
                        case EPIC:
                            manager.getEpicById(id);
                            break;
                        case SUBTASK:
                            manager.getSubtaskById(id);
                            break;
                        default:
                            manager.getTaskById(id);
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("У нас проблемы");
        }

        return manager;
    }

    private Task fromString(String value) {
        String[] taskFields = value.split(",");

        int id = Integer.parseInt(taskFields[0]);
        TaskType type = TaskType.valueOf(taskFields[1]);
        String name = taskFields[2];
        Status status = Status.valueOf(taskFields[3]);
        String description = taskFields[4];

        switch (type) {
            case SUBTASK:
                int epicId = Integer.parseInt(taskFields[5]);
                Subtask subtask = new Subtask(name, description, epicId);
                subtask.setId(id);
                subtask.setStatus(status);

                return subtask;
            case EPIC:
                Epic epic = new Epic(name, description);
                epic.setId(id);
                epic.setStatus(status);

                return epic;
            default:
                Task task = new Task(name, description);
                task.setId(id);
                task.setStatus(status);

                return task;
        }
    }

    static String historyToString(HistoryManager manager) {
        StringBuilder builder = new StringBuilder();
        List<Task> tasks = manager.getHistory();

        builder.append("id,type,name,status,description,epic\n");
        for (Task task : tasks) {
            builder.append(task.toString());
        }

        return builder.toString();
    }

    static List<Integer> historyFromString(String value) {
        List<Integer> id = new ArrayList<>();

        String[] currentHistory = value.split("\n");
        for (String line : currentHistory) {
            String[] task = line.split(",");
            id.add(Integer.parseInt(task[0]));
        }

        return id;
    }

    @Override
    public void addNewTask(Task task) {
        super.addNewTask(task);
        save();
    }

    @Override
    public void addNewEpic(Epic epic) {
        super.addNewEpic(epic);
        save();
    }

    @Override
    public void addNewSubtask(Subtask subtask) {
        super.addNewSubtask(subtask);
        save();
    }

    @Override
    public void setId(Task task, int id) {
        super.setId(task, id);
    }

    @Override
    public List<Task> getListOfTasks() {
        return super.getListOfTasks();
    }

    @Override
    public List<Epic> getListOfEpics() {
        return super.getListOfEpics();
    }

    @Override
    public List<Subtask> getListOfSubtasks() {
        return super.getListOfSubtasks();
    }

    @Override
    public void clearListOfTasks() {
        super.clearListOfTasks();
        save();
    }

    @Override
    public void clearListOfEpics() {
        super.clearListOfEpics();
        save();
    }

    @Override
    public void clearListOfSubtasks() {
        super.clearListOfSubtasks();
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void clearTasksById(int id) {
        super.clearTasksById(id);
        save();
    }

    @Override
    public void clearEpicsById(int id) {
        super.clearEpicsById(id);
        save();
    }

    @Override
    public void clearSubtasksById(int id) {
        super.clearSubtasksById(id);
        save();
    }

    @Override
    public List<Subtask> getSubtasksByEpic(Epic epic) {
        return super.getSubtasksByEpic(epic);
    }

    @Override
    public List<Task> getHistory() {
        return super.getHistory();
    }
}
