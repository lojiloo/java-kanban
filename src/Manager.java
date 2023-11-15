import java.util.ArrayList;
import java.util.HashMap;
public class Manager {
    int id = 0;
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public void addNewTask(Task task) {
        int taskId = getId();
        task.id = taskId;
        tasks.put(taskId, task);
    }

    public void addNewEpic(Epic epic) {
        int epicId = getId();
        epic.id = epicId;
        epics.put(epicId, epic);
    }

    public void addNewSubtask(Subtask subtask) {
        int subId = getId();
        subtask.id = subId;
        subtasks.put(subId, subtask);
    }

    private int getId() {
        return ++id;
    }

    public ArrayList<String> getListOfTasks() {
        ArrayList<String> listOfTasks = new ArrayList<>();
        if (!tasks.isEmpty()) {
            for (Integer tId : tasks.keySet()) {
                String tInfo = "ID задачи: " + tId + ". " + tasks.get(tId).toString();
                listOfTasks.add(tInfo);
            }
        } else {
            listOfTasks.add("Задач нет");
        }
        return listOfTasks;
    }

    public ArrayList<String> getListOfEpics() {
        ArrayList<String> listOfEpics = new ArrayList<>();
        if (!epics.isEmpty()) {
            for (Integer eId : epics.keySet()) {
                String tInfo = "ID эпика: " + eId + ". " + epics.get(eId).toString();
                listOfEpics.add(tInfo);
            }
        } else {
            listOfEpics.add("Эпиков нет");
        }
        return listOfEpics;
    }

    public ArrayList<String> getListOfSubtasks() {
        ArrayList<String> listOfSubtasks = new ArrayList<>();
        if (!subtasks.isEmpty()) {
            for (Integer sId : subtasks.keySet()) {
                String sInfo = "ID сабтаска: " + sId + ". " + subtasks.get(sId).toString();
                listOfSubtasks.add(sInfo);
            }
        } else {
            listOfSubtasks.add("Сабтасков нет");
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
            epic.subtasks.clear();
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

    public void updateTask(Task task, int id, String status) {
        task.status = status; //не понимаю, что значит "статус задачи обновляется вместе с полным обновлением задачи".
        // Решила, что это про то, что в параметрах передаётся новый статус, который присваивается переданному объекту
        tasks.put(id, task);
    }

    //а разве по условию статус эпика обновляется вручную? Я так поняла, что он вычисляется; пользователь его поменять не может

    public void updateSubtask(Subtask subtask, int id, String status) {
        Subtask sub = subtasks.get(id);
        epics.get(sub.epicId).subtasks.remove(sub);

        subtask.id = id;
        subtask.status = status;
        subtasks.put(id, subtask);

        epics.get(sub.epicId).checkStatus();
    }

    public void clearTasksById(int id) {
        tasks.remove(id);
    }

    public void clearEpicsById(int id) {
        epics.remove(id);

        ArrayList<Integer> subIdToRemove = new ArrayList<>();
        for (Subtask sub : subtasks.values()) {
            if (sub.epicId == id) {
                subIdToRemove.add(sub.id);
            }
        }

        for (int subId : subIdToRemove) {
            subtasks.remove(subId);
        }
    }

    public void clearSubtasksById(int id) {
        int epicId = subtasks.get(id).epicId;
        epics.get(epicId)
                .subtasks
                .remove(subtasks.get(id));

        subtasks.remove(id);
    }

    public ArrayList<Subtask> getSubtasksByEpic(Epic epic) {
        ArrayList<Subtask> subtasksByEpic = new ArrayList<>();

        for (Subtask subtask : subtasks.values()) {
            if (subtask.epicId == epic.id) {
                subtasksByEpic.add(subtask);
            }
        }
        return subtasksByEpic;
    }
}
