import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    ArrayList<Task> history = new ArrayList<>();

    @Override
    public void add(Task task) {
        history.add(task);

        if (history.size() > 10) {
            do {
                history.remove(0);
            } while (history.size() != 10);
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return history;
    }

}
