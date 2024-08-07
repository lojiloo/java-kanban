package managers;

import history.HistoryManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected int id = 0;
    protected Map<Integer, Task> tasks = new HashMap<>();
    protected Map<Integer, Epic> epics = new HashMap<>();
    protected Map<Integer, Subtask> subtasks = new HashMap<>();
    protected TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(task -> task.getStartTime().get()));
    protected HistoryManager history = Managers.getDefaultHistory();

    @Override
    public void addNewTask(Task task) throws IllegalArgumentException {
        if (task.getId() != 0) {
            throw new IllegalArgumentException("id не может быть установлен вручную");
        }
        int id = getId();
        task.setId(id);
        tasks.put(id, task);
    }

    @Override
    public void addNewEpic(Epic epic) throws IllegalArgumentException {
        if (epic.getId() != 0) {
            throw new IllegalArgumentException("id не может быть установлен вручную");
        }
        int id = getId();
        epic.setId(id);
        epics.put(id, epic);
    }

    @Override
    public void addNewSubtask(Subtask subtask) throws IllegalArgumentException {
        if (subtask.getId() != 0) {
            throw new IllegalArgumentException("id не может быть установлен вручную");
        }
        int id = getId();
        subtask.setId(id);

        subtasks.put(id, subtask);
        epics.get(subtask.getEpicId()).getSubtasks().add(subtask);
        epics.get(subtask.getEpicId()).checkStatus();
    }

    protected void addInternal(Task task) {
        if (task.getId() > this.id) {
            this.id = task.getId();
        }

        switch (task.getType()) {
            case TASK:
                tasks.put(task.getId(), task);

                if (task.getEndTime().isPresent()) {
                    addToPrioritizedTasks(task);
                }
                break;
            case EPIC:
                epics.put(task.getId(), (Epic) task);
                break;
            case SUBTASK:
                subtasks.put(task.getId(), (Subtask) task);
                epics.get(((Subtask) task).getEpicId()).getSubtasks().add((Subtask) task);
                epics.get(((Subtask) task).getEpicId()).checkStatus();
                epics.get(((Subtask) task).getEpicId()).checkTemporal();

                if (task.getEndTime().isPresent()) {
                    addToPrioritizedTasks(task);
                }
        }
    }

    @Override
    public void setTemporal(Task task,
                            int year, int month, int day, int hour, int min,
                            int durationMin) {

        if (task.getType() == TaskType.EPIC) {
            throw new TemporalException("Время и продолжительность эпика рассчитываются автоматически");
        }

        LocalDateTime startTime = LocalDateTime.of(year, month, day, hour, min);
        Duration duration = Duration.ofMinutes(durationMin);

        if (task.getStartTime().isPresent()) {
            task.setTemporal(startTime, duration);
            updatePrioritizedTasks(task); //обновляем, поскольку таск уже есть в prioritizedTasks
        } else {
            task.setTemporal(startTime, duration);
            addToPrioritizedTasks(task); //добавляем новый таск
        }

        if (task.getType() == TaskType.SUBTASK) {
            Subtask sub = (Subtask) task;
            epics.get(sub.getEpicId()).checkTemporal();
        }
    }

    public void removeTemporal(Task task) {
        if (task.getStartTime().isPresent()) {
            switch (task.getType()) {
                case TASK:
                    updateTask(new Task(task));
                    break;
                case SUBTASK:
                    updateSubtask(new Subtask((Subtask) task));
                    break;
                case EPIC:
                    throw new TemporalException("Время и продолжительность эпика рассчитываются автоматически");
            }
            removeFromPrioritizedTasks(task);
        }
    }

    private void updatePrioritizedTasks(Task task) {
        removeFromPrioritizedTasks(task);
        addToPrioritizedTasks(task); //чтобы была проверка при повторном добавлении
    }

    private void addToPrioritizedTasks(Task task) {
        if (!isOverlapped(task)) {
            prioritizedTasks.add(task);
        } else {
            throw new TemporalException("Данный слот времени уже занят");
        }
    }

    private void removeFromPrioritizedTasks(Task task) {
        prioritizedTasks.remove(task);
    }

    @Override
    public LinkedList<Task> getPrioritizedTasks() {
        return new LinkedList<>(prioritizedTasks);
    }

    private boolean isOverlapped(Task newTask) {
        if (!prioritizedTasks.isEmpty()) {
            return prioritizedTasks.stream()
                    .filter(task -> task.getStartTime().isPresent() && task.getEndTime().isPresent())
                    .filter(task -> task.getStartTime().get().isBefore(newTask.getEndTime().get()))
                    .anyMatch(task -> newTask.getStartTime().get().isBefore(task.getEndTime().get()));
        }
        return false;
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
            listOfTasks.addAll(new ArrayList<>(tasks.values()));
        }
        return listOfTasks;
    }

    @Override
    public List<Epic> getListOfEpics() {
        List<Epic> listOfEpics = new ArrayList<>();
        if (!epics.isEmpty()) {
            listOfEpics.addAll(new ArrayList<>(epics.values()));
        }
        return listOfEpics;
    }

    @Override
    public List<Subtask> getListOfSubtasks() {
        List<Subtask> listOfSubtasks = new ArrayList<>();
        if (!subtasks.isEmpty()) {
            listOfSubtasks.addAll(new ArrayList<>(subtasks.values()));
        }
        return listOfSubtasks;
    }

    @Override
    public void clearListOfTasks() {
        tasks.keySet().forEach(id -> history.remove(id));
        tasks.values().forEach(this::removeFromPrioritizedTasks);
        tasks.clear();
    }

    @Override
    public void clearListOfEpics() {
        epics.keySet().forEach(id -> history.remove(id));
        clearListOfSubtasks();
        epics.clear();
    }

    @Override
    public void clearListOfSubtasks() {
        epics.values().forEach(Epic::clearSubtasks);
        epics.values().forEach(Epic::checkStatus);
        epics.values().forEach(Epic::checkTemporal);

        subtasks.keySet().forEach(id -> history.remove(id));
        subtasks.values().forEach(this::removeFromPrioritizedTasks);
        subtasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        history.add(tasks.get(id));
        return tasks.get(id);
    }

    public Task getTaskById(int id, boolean updateHistory) {
        if (updateHistory) {
            return getTaskById(id);
        } else {
            return tasks.get(id);
        }
    }

    @Override
    public Epic getEpicById(int id) {
        history.add(epics.get(id));
        return epics.get(id);
    }

    public Epic getEpicById(int id, boolean updateHistory) {
        if (updateHistory) {
            return getEpicById(id);
        } else {
            return epics.get(id);
        }
    }

    @Override
    public Subtask getSubtaskById(int id) {
        history.add(subtasks.get(id));
        return subtasks.get(id);
    }

    public Subtask getSubtaskById(int id, boolean updateHistory) {
        if (updateHistory) {
            return getSubtaskById(id);
        } else {
            return subtasks.get(id);
        }
    }

    @Override
    public void updateTask(Task task) {
        if (task.getStartTime().isPresent()) {
            updatePrioritizedTasks(task);
        }

        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        Subtask subToRemove = subtasks.get(subtask.getId());
        epics.get(subToRemove.getEpicId()).getSubtasks().remove(subToRemove);

        epics.get(subtask.getEpicId()).getSubtasks().add(subtask);
        epics.get(subtask.getEpicId()).checkStatus();
        epics.get(subtask.getEpicId()).checkTemporal();

        if (subtask.getStartTime().isPresent()) {
            updatePrioritizedTasks(subtask);
        }

        subtasks.put(subtask.getId(), subtask);
    }

    @Override
    public void clearTasksById(int id) {
        if (tasks.get(id).getStartTime().isPresent()) {
            removeFromPrioritizedTasks(tasks.get(id));
        }

        history.remove(id);
        tasks.remove(id);
    }

    @Override
    public void clearEpicsById(int id) {
        epics.remove(id);
        history.remove(id);

        List<Integer> subIdToRemove = subtasks.values().stream()
                .filter(subtask -> subtask.getEpicId() == id)
                .map(Task::getId)
                .collect(Collectors.toList());

        subIdToRemove.stream()
                .filter(subId -> subtasks.get(subId).getStartTime().isPresent())
                .forEach(subId -> removeFromPrioritizedTasks(subtasks.get(subId)));
        subIdToRemove.forEach(subId -> history.remove(subId));
        subIdToRemove.forEach(subId -> subtasks.remove(subId));
    }

    @Override
    public void clearSubtasksById(int id) {
        int epicId = subtasks.get(id).getEpicId();
        epics.get(epicId)
                .getSubtasks()
                .remove(subtasks.get(id));
        epics.get(epicId).checkStatus();
        epics.get(epicId).checkTemporal();

        if (subtasks.get(id).getStartTime().isPresent()) {
            removeFromPrioritizedTasks(subtasks.get(id));
        }
        history.remove(id);
        subtasks.remove(id);
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