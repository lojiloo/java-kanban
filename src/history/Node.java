package history;

import tasks.Task;

public class Node {
    Task data;
    Node next;
    Node prev;

    public Node(Task data, Node next, Node prev) {
        this.data = data;
        this.next = next;
        this.prev = prev;
    }
}


