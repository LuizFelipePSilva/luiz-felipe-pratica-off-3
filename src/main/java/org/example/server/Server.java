package org.example.server;

import org.example.client.SplayTree;
import java.util.concurrent.ThreadLocalRandom;

import org.example.client.SplayNode;
import org.example.movie.Movie;
import org.example.movie.Category;

public class Server {
  LinkedList list;
  HashTable hashTable;
  SplayTree preferences;

  public int lastComparisonCount = 0;

  public Server() {
    this.list = new LinkedList();
    this.preferences = new SplayTree();
    this.hashTable = new HashTable();
  }

  public ListNode addMovie(Movie m) {
    ListNode a = list.addMovie(m);
    hashTable.put(m.getId(), a);
    return a;
  }

  public Movie searchWithIndex(int index) {
    lastComparisonCount = 1;
    ListNode node = hashTable.get(index);
    if (node == null)
      return null;
    preferences.insert(index, node.data.getTitle());
    return node.data;
  }

  public Movie searchWithoutIndex(int id) {
    lastComparisonCount = 0;
    ListNode current = list.getHead();
    while (current != null) {
      lastComparisonCount++;
      if (current.data.getId() == id)
        return current.data;
      current = current.next;
    }
    return null;
  }

  public Movie searchByCategory(Category movie) {
    int num = movie.ordinal();
    int block = ThreadLocalRandom.current().nextInt(2);
    int start = (block * 500) + (num * 50);
    int end = start + 50;
    int randomIndex = ThreadLocalRandom.current().nextInt(start, end + 1);

    return searchWithIndex(randomIndex);
  }

  public SplayNode[] getTopTenMovies() {
    return preferences.getTopN(10);
  }

  public SplayNode getTopMovie() {
    return preferences.getRoot();
  }
}
