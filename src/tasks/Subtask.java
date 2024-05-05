package tasks;

import managers.TaskType;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
        this.type = TaskType.SUBTASK;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return id
                + ",SUBTASK,"
                + name + ","
                + status + ","
                + description + ","
                + startTime + ","
                + duration + ","
                + epicId + ",\n";
    }
}
