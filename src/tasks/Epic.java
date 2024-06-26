package tasks;

import managers.TaskType;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    //protected TaskType type = TaskType.EPIC;
    private final List<Subtask> subtasks = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
        this.type = TaskType.EPIC;
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public void checkStatus() {
        if (!subtasks.isEmpty()) {
            int counterNew = 0;
            int counterDone = 0;

            for (Subtask subtask : subtasks) {
                switch (subtask.status) {
                    case NEW:
                        ++counterNew;
                        break;
                    case DONE:
                        ++counterDone;
                        break;
                }

                if (counterDone == subtasks.size()) {
                    this.status = Status.DONE;
                } else if (counterNew == subtasks.size()) {
                    this.status = Status.NEW;
                } else {
                    this.status = Status.IN_PROGRESS;
                }
            }

        } else {
            this.status = Status.NEW;
        }
    }

    @Override
    public String toString() {
        return id + ",EPIC," + name + "," + status + "," + description + ",\n";
    }
}
