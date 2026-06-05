package org.example.client;

import org.example.client.LRUList.LRUNode;
import org.example.movie.Movie;

public class CacheManager {
  HashTableClient hashTable;
  private LRUList evictedList = new LRUList();

  public CacheManager() {
    this.hashTable = new HashTableClient() {
      @Override
      public Movie evict() {
        Movie evicted = super.evict();
        if (evicted != null)
          evictedList.addToFront(new LRUNode(evicted));
        return evicted;
      }
    };
  }

  public void add(Movie movie) {
    hashTable.put(movie);
  }

  public Movie search(int id) {
    return hashTable.get(id);
  }

  public Movie[] getRecentTen() {
    Movie[] result = new Movie[10];
    LRUNode current = hashTable.getList().getHead();
    int i = 0;
    while (current != null && i < 10) {
      result[i++] = current.movie;
      current = current.next;
    }
    return result;
  }

  public LRUList getEvictedList() {
    return evictedList;
  }

  public int getCacheSize() {
    return hashTable.getSize();
  }
}
