package history;

import tasks.Task;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private Node<Task> head;
    private Node<Task> tail;
    private Map<Integer, Node<Task>> mapping = new LinkedHashMap<>();

    @Override
    public void add(Task task) {
        if (task != null) {
            linkLast(task);
        }
    }

    public void linkLast(Task task) {

        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<>(task, null, oldTail);
        tail = newNode;

        if (mapping.containsKey(task.getId()) && mapping.size() > 1) {
            remove(task.getId());
        }

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

    public void removeNode(Node<Task> node) {

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

        for (Node<Task> node : mapping.values()) {
            historyList.add(node.data);
        }

        return historyList;
    }
}
