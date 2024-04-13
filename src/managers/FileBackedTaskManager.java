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

    public FileBackedTaskManager(String path) {
        this.path = path;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file.getAbsolutePath());

        try {
            String content = Files.readString(Path.of(file.getAbsolutePath()));
            String[] lines = content.split("\n");

            List<Task> memory = new ArrayList<>();
            int lineCounter = 1;
            for (int i = 1; i < lines.length; i++) {
                if (!lines[i].equals("IN_MEMORY_HISTORY_MANAGER")) {
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
                List<Integer> idInHistory = historyFromString(lines[lineCounter + 1]);

                for (Task task : memory) {
                    for (int id : idInHistory) {
                        if (task.getId() == id) {
                            TaskType type = task.getType();

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
                }
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка менеджера: ошибка при сохранении в файл", e);
        }

        return manager;
    }

    private static String historyToString(HistoryManager manager) {
        StringBuilder builder = new StringBuilder();
        List<Task> tasks = manager.getHistory();

        builder.append("IN_MEMORY_HISTORY_MANAGER\n");
        for (Task task : tasks) {
            builder.append(task.getId()).append(",");
        }

        return builder.toString();
    }

    private static List<Integer> historyFromString(String value) {
        List<Integer> idList = new ArrayList<>();
        String[] idListFromFile = value.split(",");

        for (String id : idListFromFile) {
            idList.add(Integer.parseInt(id));
        }

        return idList;
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
            throw new ManagerSaveException("Ошибка менеджера: ошибка при сохранении в файл", e);
        }
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
