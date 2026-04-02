package com.mycompany.tarumtlibraryservices.adt;

/**
 * Generic Node for linked list implementation
 *author [Lim Chuin Hao]
 * @param <T> the type of data stored in the node
 */
public class Node<T> {

    public T data;      // public for direct access (performance)
    public Node<T> next;

    public Node(T data) {
        this.data = data;
        this.next = null;
    }
}
