package history;

import tasks.Task;
import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private List<Task> history = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (task != null) {
            history.add(Task.copyOf(task));
        }

        if (history.size() > 10) {
            history.subList(0,1).clear();
        }

        /* именно для ArrayList не нашла такого метода, который элегантно бы заменил list.remove(0). что-то
        такое нашлось только для LinkedList... ну и есть методы в ArrayList, задающие min размер, но нигде
        не нашла такого, чтобы ограничивал max (разве что самой прописать, но будет ли это оправданно, если
        всегда можно просто удалить первый элемент через нулевой индекс...). В общем, мне нужен волшебный пинок в
        нужную сторону, если это не та сторона :( */
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }

}
