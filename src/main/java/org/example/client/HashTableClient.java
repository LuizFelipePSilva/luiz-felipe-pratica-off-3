package org.example.client;

import org.example.client.LRUList.LRUNode;
import org.example.movie.Movie;

public class HashTableClient {
  private final int SIZE = 53;
  private final int CAPACITY = 50;

  public Entry[] buckets;
  public LRUList list;

  public static class Entry {
    int key;
    LRUNode node;
    Entry next;

    public Entry(int key, LRUNode node) {
      this.key = key;
      this.node = node;
    }
  }

  public HashTableClient() {
    this.buckets = new Entry[SIZE];
    this.list = new LRUList();
  }

  public int hash(int key) {
    return key % SIZE;
  }

  public void put(Movie movie) {
    if (list.getSize() >= CAPACITY) {
      int LRU = evict().getId();
      System.out.println("Id of item removed from cache it's: " + LRU);
    }

    int key = movie.getId();
    int index = hash(key);
    Entry current = buckets[index];
    while (current != null) {
      if (current.key == key)
        return;
      current = current.next;
    }
    LRUNode node = new LRUNode(movie);
    list.addToFront(node);
    Entry entry = new Entry(key, node);
    entry.next = buckets[index];
    buckets[index] = entry;
  }

  public Movie get(int key) {
    int index = hash(key);
    Entry current = buckets[index];
    while (current != null) {
      if (current.key == key) {
        list.moveToFront(current.node);
        return current.node.movie;
      }
      current = current.next;
    }
    return null;
  }

  public Movie evict() {
    LRUNode evicted = list.removeLast();
    if (evicted == null)
      return null;
    int key = evicted.movie.getId();
    int index = hash(key);
    Entry current = buckets[index];
    Entry prev = null;
    while (current != null) {
      if (current.key == key) {
        if (prev == null)
          buckets[index] = current.next;
        else
          prev.next = current.next;
        break;
      }
      prev = current;
      current = current.next;
    }
    return evicted.movie;
  }

  public LRUList getList() {
    return list;
  }

  public int getSize() {
    return list.getSize();
  }
}
