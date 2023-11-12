import java.util.HashMap;
public class Epic extends Task {
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int subtaskId = 0;

    public Epic(String name, String description) {
        super(name, description);
    }

    public void createSubtask(String name, String description) {
        int id = assignSubtaskId();
        Subtask subtask = new Subtask(name, description, id);
        subtasks.put(id, subtask);
    }

    private int assignSubtaskId() {
        return ++subtaskId;
    }

    public void updateSubtask(String subtaskName) {
        for (Subtask subtask : subtasks.values()) {
            if (subtask.subtaskName.equals(subtaskName)) {
                if (subtask.subtaskStatus.equals("NEW")) {
                    subtask.subtaskStatus = "IN_PROGRESS";
                } else if (subtask.subtaskStatus.equals("IN_PROGRESS")) {
                    subtask.subtaskStatus = "DONE";
                }
            }
            checkStatus();
        }
    }

    public void checkStatus() {
        int counterNew = 0;
        int counterDone = 0;

        for (Subtask subtask : subtasks.values()) {
            switch (subtask.subtaskStatus) {
                case "NEW" :
                    ++counterNew;
                    break;
                case "DONE" :
                    ++counterDone;
                    break;
            }
        }

        if (counterDone == subtasks.size()) {
            status = "DONE";
        } else if (counterNew == subtasks.size()) {
            status = "NEW";
        } else {
            status = "IN_PROGRESS";
        }
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public class Subtask {
        String subtaskName;
        String subtaskDescription;
        String subtaskStatus;
        int subtaskId;

        public Subtask(String name, String description, int id) {
            this.subtaskName = name;
            this.subtaskDescription = description;
            this.subtaskStatus = "NEW";
            this.subtaskId = id;
        }

        @Override
        public String toString() {
            return "ID cабтаска: " + subtaskId
                    + ". Название: " + subtaskName
                    + ". Описание: " + subtaskDescription
                    + ". Статус: " + subtaskStatus + " ";
        }
    }
}
