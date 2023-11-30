package managers;

import tasks.*;
import java.util.List;

public interface TaskManager {

    void addNewTask(Task task);

    void addNewEpic(Epic epic);

    void addNewSubtask(Subtask subtask);

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

    //void add(Task task);
}