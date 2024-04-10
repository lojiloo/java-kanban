package history;

import tasks.Task;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private Node head;
    private Node tail;
    private final Map<Integer, Node> mapping = new LinkedHashMap<>();

    @Override
    public void add(Task task) {
        if (task != null) {
            linkLast(task);
        }
    }

    public void linkLast(Task task) {

        final Node oldTail = tail;
        final Node newNode = new Node(task, null, oldTail);

        if (mapping.containsKey(task.getId())) {
            remove(task.getId());
        }

        tail = newNode;

        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }

        mapping.put(task.getId(), newNode);
    }

    @Override
    public void remove(int id) {
        if (mapping.containsKey(id)) {
            removeNode(mapping.get(id));
        }
    }

    public void removeNode(Node node) {

        if (mapping.size() == 1) {
            head = null;
            tail = null;
            mapping.remove(node.data.getId());
            return;
        }

        mapping.remove(node.data.getId());

        if (node.prev == null) {
            head = node.next;
            node.next.prev = null;
        } else if (node.next == null) {
            tail = node.prev;
            node.prev.next = null;
        } else {
            node.next.prev = node.prev;
            node.prev.next = node.next;
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> historyList = new ArrayList<>();

        for (Node node : mapping.values()) {
            historyList.add(node.data);
        }

        return historyList;
    }
}
