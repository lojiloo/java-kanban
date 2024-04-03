package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

public interface TaskManager {

    void addNewTask(Task task);

    void addNewEpic(Epic epic);

    void addNewSubtask(Subtask subtask);

    void setId(Task task, int id);

    List<Task> getListOfTasks();

    List<Epic> getListOfEpics();

    List<Subtask> getListOfSubtasks();

    void clearListOfTasks();

    void clearListOfEpics();

    void clearListOfSubtasks();

    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void clearTasksById(int id);

    void clearEpicsById(int id);

    void clearSubtasksById(int id);

    List<Subtask> getSubtasksByEpic(Epic epic);

    List<Task> getHistory();
}
