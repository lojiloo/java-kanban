import java.util.ArrayList;
import java.util.HashMap;
public class Manager {
    int id = 0;
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public void addNewTask(Task task) {
        int id = getId();
        task.setId(id);
        tasks.put(id, task);
    }

    public void addNewEpic(Epic epic) {
        int id = getId();
        epic.setId(id);
        epics.put(id, epic);
    }

    public void addNewSubtask(Subtask subtask) {
        int id = getId();
        subtask.setId(id);
        subtasks.put(id, subtask);

        epics.get(subtask.getEpicId()).getSubtasks().add(subtask);
        epics.get(subtask.getEpicId()).checkStatus();
    }

    private int getId() {
        return ++id;
    }

    public ArrayList<Task> getListOfTasks() {
        ArrayList<Task> listOfTasks = new ArrayList<>();
        if (!tasks.isEmpty()) {
            for (Integer id : tasks.keySet()) {
                Task task = tasks.get(id);
                listOfTasks.add(task);
            }
        }
        return listOfTasks;
    }

    public ArrayList<Epic> getListOfEpics() {
        ArrayList<Epic> listOfEpics = new ArrayList<>();
        if (!epics.isEmpty()) {
            for (Integer id : epics.keySet()) {
                Epic epic = epics.get(id);
                listOfEpics.add(epic);
            }
        }
        return listOfEpics;
    }

    public ArrayList<Subtask> getListOfSubtasks() {
        ArrayList<Subtask> listOfSubtasks = new ArrayList<>();
        if (!subtasks.isEmpty()) {
            for (Integer id : subtasks.keySet()) {
                Subtask subtask = subtasks.get(id);
                listOfSubtasks.add(subtask);
            }
        }
        return listOfSubtasks;
    }

    public void clearListOfTasks() {
        tasks.clear();
    }

    public void clearListOfEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void clearListOfSubtasks() {
        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
            epic.checkStatus();
        }
        subtasks.clear();
    }

    public Task getTaskById(int id) {
            return tasks.get(id);
    }

    public Epic getEpicById(int id) {
            return epics.get(id);
    }

    public Subtask getSubtaskById(int id) {
            return subtasks.get(id);
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void updateSubtask(Subtask subtask) {
        Subtask sub = subtasks.get(subtask.getId());

        epics.get(sub.getEpicId()).getSubtasks().remove(sub);
        epics.get(subtask.getEpicId()).getSubtasks().add(subtask);
        epics.get(subtask.getEpicId()).checkStatus();

        subtasks.put(subtask.getId(), subtask);
    }

    public void clearTasksById(int id) {
        tasks.remove(id);
    }

    public void clearEpicsById(int id) {
        epics.remove(id);

        ArrayList<Integer> subIdToRemove = new ArrayList<>();
        for (Subtask sub : subtasks.values()) {
            if (sub.getEpicId() == id) {
                subIdToRemove.add(sub.getId());
            }
        }

        for (int subId : subIdToRemove) {
            subtasks.remove(subId);
        }
    }

    public void clearSubtasksById(int id) {
        int epicId = subtasks.get(id).getEpicId();
        epics.get(epicId)
                .getSubtasks()
                .remove(subtasks.get(id));

        subtasks.remove(id);
        epics.get(epicId).checkStatus();
    }

    public ArrayList<Subtask> getSubtasksByEpic(Epic epic) {
        return epic.getSubtasks();
    }
}