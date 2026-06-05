package org.example.server;

public class HashTable {
  private final int SIZE = 1013;
  private ListNode[] buckets;

  public HashTable() {
    buckets = new ListNode[SIZE];
  }

  private int hash(int id) {
    return id % SIZE;
  }

  public void put(int id, ListNode node) {
    int index = hash(id);

    ListNode current = buckets[index];

    while (current != null) {
      if (current.data.getId() == id)
        return;
      current = current.next;
    }

    node.next = buckets[index];
    buckets[index] = node;
  }

  public ListNode get(int id) {
    int index = hash(id);
    ListNode prev = null;
    ListNode current = buckets[index];

    while (current != null) {
      if (current.data.getId() == id) {

        if (prev != null) {
          prev.next = current.next;
          current.next = buckets[index];
          buckets[index] = current;
        }
        return current;
      }

      prev = current;
      current = current.next;
    }

    return null;
  }
}
