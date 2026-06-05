package org.example.client;

import org.example.movie.Movie;

public class LRUList {

  public static class LRUNode {
    public Movie movie;
    public LRUNode prev, next;

    public LRUNode(Movie key) {
      this.movie = key;
    }
  }

  private LRUNode head;
  private LRUNode tail;
  private int size;

  public void addToFront(LRUNode node) {
    node.prev = null;
    node.next = head;
    if (head != null)
      head.prev = node;
    head = node;
    if (tail == null)
      tail = node;
    size++;
  }

  public void moveToFront(LRUNode node) {
    if (node == head)
      return;
    remove(node);
    size++;
    node.prev = null;
    node.next = head;
    if (head != null)
      head.prev = node;
    head = node;
    if (tail == null)
      tail = node;
  }

  public LRUNode removeLast() {
    if (tail == null)
      return null;
    LRUNode evicted = tail;
    remove(tail);
    return evicted;
  }

  public void remove(LRUNode node) {
    if (node.prev != null)
      node.prev.next = node.next;
    else
      head = node.next;
    if (node.next != null)
      node.next.prev = node.prev;
    else
      tail = node.prev;
    node.prev = null;
    node.next = null;
    size--;
  }

  public LRUNode getHead() {
    return head;
  }

  public LRUNode getTail() {
    return tail;
  }

  public int getSize() {
    return size;
  }
}
