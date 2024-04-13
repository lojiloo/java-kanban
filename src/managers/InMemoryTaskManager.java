package managers;

import history.HistoryManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    protected int id = 0;
    protected Map<Integer, Task> tasks = new HashMap<>();
    protected Map<Integer, Epic> epics = new HashMap<>();
    protected Map<Integer, Subtask> subtasks = new HashMap<>();
    protected HistoryManager history = Managers.getDefaultHistory();

    @Override
    public void addNewTask(Task task) {
        if (task.getId() == 0) {
            int id = getId();
            setId(task, id);
        } else {
            this.id = task.getId();
            setId(task, task.getId());
        }
        tasks.put(task.getId(), task);
    }

    @Override
    public void addNewEpic(Epic epic) {
        if (epic.getId() == 0) {
            int id = getId();
            setId(epic, id);
        } else {
            setId(epic, epic.getId());
        }
        epics.put(epic.getId(), epic);
    }

    @Override
    public void addNewSubtask(Subtask subtask) {
        if (subtask.getId() == 0) {
            int id = getId();
            setId(subtask, id);
        } else {
            this.id = subtask.getId();
            setId(subtask, subtask.getId());
        }
        subtasks.put(subtask.getId(), subtask);

        epics.get(subtask.getEpicId()).getSubtasks().add(subtask);
        epics.get(subtask.getEpicId()).checkStatus();
    }

    protected int getId() {
        return ++id;
    }

    public void setId(Task task, int id) {

        if (id != this.id) {
            if (id > this.id) {
                this.id = id;
            }
        }

        if (tasks.get(id) != null) {
            setId(tasks.get(id), id + 1);
            tasks.put(id + 1, tasks.get(id));
        } else if (epics.get(id) != null) {
            setId(epics.get(id), id + 1);
            epics.put(id + 1, epics.get(id));
        } else if (subtasks.get(id) != null) {
            setId(subtasks.get(id), id + 1);
            subtasks.put(id + 1, subtasks.get(id));
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

        for (int id : tasks.keySet()) {
            history.remove(id);
        }

        tasks.clear();
    }

    @Override
    public void clearListOfEpics() {

        for (int id : epics.keySet()) {
            history.remove(id);
        }
        epics.clear();

        for (int id : subtasks.keySet()) {
            history.remove(id);
        }
        subtasks.clear();
    }

    @Override
    public void clearListOfSubtasks() {
        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
            epic.checkStatus();
        }

        for (int id : subtasks.keySet()) {
            history.remove(id);
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
        history.remove(id);
        tasks.remove(id);
    }

    @Override
    public void clearEpicsById(int id) {
        epics.remove(id);
        history.remove(id);

        ArrayList<Integer> subIdToRemove = new ArrayList<>();
        for (Subtask sub : subtasks.values()) {
            if (sub.getEpicId() == id) {
                subIdToRemove.add(sub.getId());
            }
        }

        for (int subId : subIdToRemove) {
            subtasks.remove(subId);
            history.remove(subId);
        }
    }

    @Override
    public void clearSubtasksById(int id) {
        int epicId = subtasks.get(id).getEpicId();
        epics.get(epicId)
                .getSubtasks()
                .remove(subtasks.get(id));

        subtasks.remove(id);
        history.remove(id);
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