package history;

public class Node<T> {
    T data;
    Node<T> next;
    Node<T> prev;

    public Node(T data, Node<T> next, Node<T> prev) {
        this.data = data;
        this.next = next;
        this.prev = prev;
    }
}


