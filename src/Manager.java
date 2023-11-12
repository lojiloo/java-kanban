import java.util.ArrayList;
import java.util.HashMap;
public class Manager {
    int id = 0;
    HashMap<Integer, Task> tasks = new HashMap<>(); //здесь все задачи
    HashMap<Integer, Epic> epics = new HashMap<>(); //здесь все эпики (внутри них - сабтаски)

    public void createNewTask (Task task) {
        int taskId = assignId();
        task.id = taskId;
        tasks.put(taskId, task);
    }

    public void createNewEpic(Epic epic) {
        int epicId = assignId();
        epic.id = epicId;
        epics.put(epicId, epic);
    }

    private int assignId() {
        return ++id;
    }

    public void createNewSubtask(Epic epic, String name, String description) {
        epic.createSubtask(name, description);
    }

    public ArrayList<String> showListOfTasksAndEpics() {
        ArrayList<String> totalList = new ArrayList<>();

        if (!tasks.isEmpty()) {
            for (Integer tId : tasks.keySet()) {
                String tInfo = "ID задачи: " + tId + ". " + tasks.get(tId).toString();
                totalList.add(tInfo);
            }
        } else {
            totalList.add("Задач нет");
        }

        if (!epics.isEmpty()) {
            for (Integer eId : epics.keySet()) {
                String eInfo = "\nID эпика: " + eId + ". " + epics.get(eId).toString();
                String subInfo = "";

                if (!epics.get(eId).getSubtasks().isEmpty()) {
                    for (Integer subId : epics.get(eId).getSubtasks().keySet()) {
                        subInfo += epics.get(eId)
                                .getSubtasks()
                                .get(subId)
                                .toString();
                    }
                } else {
                    subInfo += "Сабтасков нет";
                }
                totalList.add(eInfo);
                totalList.add(subInfo);
            }
        } else {
            totalList.add("\nЭпиков нет");
        }
        return totalList;
    }

    public String getById(int id) {
        if (tasks.containsKey(id)) {
            return tasks.get(id).toString();
        } else if (epics.containsKey(id)) {
            return epics.get(id).toString() + ", " + epics.get(id).getSubtasks().toString();
        } else {
            return "Задачи/эпика c id " + id + " нет.";
        }
    }

    public void updateTask(Task newTask) {
        for (Task task : tasks.values()) {
            if (task.equals(newTask)) {
                task.updateStatus();
            }
        }
    }

    public void updateEpic(String epicName, String subtaskName) {
        for (Epic epic : epics.values()) {
            if (epic.name.equals(epicName)) {
                epic.updateSubtask(subtaskName);
            }
        }
    }

    public void clearLists() {
        tasks.clear();
        epics.clear();
    }

    public void clearListsById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else if (epics.containsKey(id)) {
            epics.remove(id);
        } else {
            System.out.println("Задачи/эпика c id " + id + " нет.");
        }
    }
}
