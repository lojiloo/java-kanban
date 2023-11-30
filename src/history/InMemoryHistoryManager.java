package history;

import tasks.Task;
import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private List<Task> history = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (task != null) {
            history.add(task);
        } else {
            return;
        }

        if (history.size() > 10) {
                history.remove(0);
        }
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }

}
