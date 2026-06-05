package org.example.server;

import org.example.movie.Movie;

public class LinkedList {
    private ListNode head;
    private ListNode tail;
    private int size;

    public ListNode addMovie(Movie m) {
        ListNode node = new ListNode(m);
        if (head == null) {
            head = node;
            tail = node;
        } else {
            tail.next = node;
            tail = node;
        }
        size++;
        return node;
    }

    public int getSize() {
        return size;
    }

    public ListNode getHead() {
        return head;
    }
}
