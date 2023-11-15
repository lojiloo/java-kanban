import java.util.ArrayList;

public class Epic extends Task {
    ArrayList<Subtask> subtasks = new ArrayList<>();
    public Epic(String name, String description) {
        super(name, description);
    }

    public void checkStatus() {
        if (!subtasks.isEmpty()) {
            int counterNew = 0;
            int counterDone = 0;

            for (Subtask subtask : subtasks) {
                switch (subtask.status) {
                    case "NEW" :
                        ++counterNew;
                        break;
                    case "DONE" :
                        ++counterDone;
                        break;
                }

                if (counterDone == subtasks.size()) {
                    this.status = "DONE";
                } else if (counterNew == subtasks.size()) {
                    this.status = "NEW";
                } else {
                    this.status = "IN_PROGRESS";
                }
            }

        } else {
            this.status = "NEW";
        }
    }

}
