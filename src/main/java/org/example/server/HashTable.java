package org.example.server;

import org.example.connection.Channel;
import org.example.connection.Message;
import org.example.connection.Channel.TransmissionResult;

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

  public ListNode get(TransmissionResult request) {
    Message reqMsg = Channel.receive(request);
    int id = Integer.parseInt(reqMsg.getContent());

    int index = hash(id);
    ListNode prev = null;
    ListNode current = buckets[index];
    ListNode found = null;

    while (current != null) {
      if (current.data.getId() == id) {

        if (prev != null) {
          prev.next = current.next;
          current.next = buckets[index];
          buckets[index] = current;
        }
        found = current;
        break;
      }

      prev = current;
      current = current.next;
    }

    String content = (found != null) ? id + "|" + found.data.getTitle() : "MISS";
    TransmissionResult resp = Channel.send(new Message(Message.Type.HASH_RESPONSE, content));
    Channel.printMetrics(resp);

    return found;
  }
}
