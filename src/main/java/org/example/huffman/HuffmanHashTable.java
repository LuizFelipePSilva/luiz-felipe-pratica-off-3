package org.example.huffman;

public class HuffmanHashTable {

  private static class Entry {
    int key;
    String value;
    Entry next;

    Entry(int key, String value) {
      this.key = key;
      this.value = value;
    }
  }

  private final int SIZE = 256;
  private Entry[] buckets;

  public HuffmanHashTable() {
    buckets = new Entry[SIZE];
  }

  private int hash(int key) {
    return key % SIZE;
  }

  public void put(int key, String value) {
    int index = hash(key);
    Entry current = buckets[index];
    while (current != null) {
      if (current.key == key) {
        current.value = value;
        return;
      }
      current = current.next;
    }
    Entry newEntry = new Entry(key, value);
    newEntry.next = buckets[index];
    buckets[index] = newEntry;
  }

  public String get(int key) {
    int index = hash(key);
    Entry current = buckets[index];
    while (current != null) {
      if (current.key == key)
        return current.value;
      current = current.next;
    }
    return null;
  }
}
