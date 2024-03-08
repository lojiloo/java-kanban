package managers;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import history.*;
import tasks.*;

public class InMemoryTaskManager implements TaskManager {
    protected int id = 0;
    protected Map<Integer, Task> tasks = new HashMap<>();
    protected Map<Integer, Epic> epics = new HashMap<>();
    protected Map<Integer, Subtask> subtasks = new HashMap<>();
    protected HistoryManager history = Managers.getDefaultHistory();

    @Override
    public void addNewTask(Task task) {
        int id = getId();
        task.setId(id);
        tasks.put(id, task);
    }

    @Override
    public void addNewEpic(Epic epic) {
        int id = getId();
        epic.setId(id);
        epics.put(id, epic);
    }

    @Override
    public void addNewSubtask(Subtask subtask) {
        int id = getId();
        subtask.setId(id);
        subtasks.put(id, subtask);

        epics.get(subtask.getEpicId()).getSubtasks().add(subtask);
        epics.get(subtask.getEpicId()).checkStatus();
    }

    protected int getId() {
        return ++id;
    }

    public void setId(Task task, int id) {

        if (id <= this.id) {
            if (getEpicById(id) != null) {
                getEpicById(id).setId(++this.id);
            } else if (getTaskById(id) != null) {
                getTaskById(id).setId(++this.id);
            } else if (getSubtaskById(id) != null) {
                getSubtaskById(id).setId(++this.id);
            }
        }

        task.setId(id);
    }

    @Override
    public List<Task> getListOfTasks() {
        List<Task> listOfTasks = new ArrayList<>();
        if (!tasks.isEmpty()) {
            for (Integer id : tasks.keySet()) {
                Task task = tasks.get(id);
                listOfTasks.add(task);
            }
        }
        return listOfTasks;
    }

    @Override
    public List<Epic> getListOfEpics() {
        List<Epic> listOfEpics = new ArrayList<>();
        if (!epics.isEmpty()) {
            for (Integer id : epics.keySet()) {
                Epic epic = epics.get(id);
                listOfEpics.add(epic);
            }
        }
        return listOfEpics;
    }

    @Override
    public List<Subtask> getListOfSubtasks() {
        List<Subtask> listOfSubtasks = new ArrayList<>();
        if (!subtasks.isEmpty()) {
            for (Integer id : subtasks.keySet()) {
                Subtask subtask = subtasks.get(id);
                listOfSubtasks.add(subtask);
            }
        }
        return listOfSubtasks;
    }

    @Override
    public void clearListOfTasks() {
        tasks.clear();
    }

    @Override
    public void clearListOfEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void clearListOfSubtasks() {
        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
            epic.checkStatus();
        }
        subtasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
            history.add(tasks.get(id));
            return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
            history.add(epics.get(id));
            return epics.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
            history.add(subtasks.get(id));
            return subtasks.get(id);
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        Subtask sub = subtasks.get(subtask.getId());

        epics.get(sub.getEpicId()).getSubtasks().remove(sub);
        epics.get(subtask.getEpicId()).getSubtasks().add(subtask);
        epics.get(subtask.getEpicId()).checkStatus();

        subtasks.put(subtask.getId(), subtask);
    }

    @Override
    public void clearTasksById(int id) {
        tasks.remove(id);
    }

    @Override
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

    @Override
    public void clearSubtasksById(int id) {
        int epicId = subtasks.get(id).getEpicId();
        epics.get(epicId)
                .getSubtasks()
                .remove(subtasks.get(id));

        subtasks.remove(id);
        epics.get(epicId).checkStatus();
    }

    @Override
    public List<Subtask> getSubtasksByEpic(Epic epic) {
        return epic.getSubtasks();
    }

    @Override
    public List<Task> getHistory() {
        return history.getHistory();
    }

}