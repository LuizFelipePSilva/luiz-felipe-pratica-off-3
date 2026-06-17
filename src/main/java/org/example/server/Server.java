package org.example.server;

import org.example.client.SplayTree;
import java.util.concurrent.ThreadLocalRandom;

import org.example.client.SplayNode;
import org.example.movie.Movie;
import org.example.movie.Category;
import org.example.connection.Channel;
import org.example.connection.Message;
import org.example.connection.Channel.TransmissionResult;

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

  public Movie searchWithIndex(TransmissionResult request) {
    lastComparisonCount = 1;

    Message reqMsg = Channel.receive(request);
    int index = Integer.parseInt(reqMsg.getContent());

    TransmissionResult hashReq = Channel.send(new Message(Message.Type.HASH_LOOKUP, String.valueOf(index)));
    Channel.printMetrics(hashReq);

    ListNode node = hashTable.get(hashReq);
    if (node == null)
      return null;

    String content = index + "|" + node.data.getTitle();
    TransmissionResult t = Channel.send(new Message(Message.Type.SPLAY_INSERT, content));
    Channel.printMetrics(t);

    preferences.insert(index, node.data.getTitle());
    return node.data;
  }

  public Movie searchWithoutIndex(TransmissionResult request) {
    Message reqMsg = Channel.receive(request);
    int id = Integer.parseInt(reqMsg.getContent());

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

  public Movie searchByCategory(TransmissionResult request) {
    Message reqMsg = Channel.receive(request);
    Category category = Category.valueOf(reqMsg.getContent());

    int num = category.ordinal();
    int block = ThreadLocalRandom.current().nextInt(2);
    int start = (block * 500) + (num * 50);
    int end = start + 50;
    int randomIndex = ThreadLocalRandom.current().nextInt(start, end + 1);

    TransmissionResult indexReq = Channel.send(new Message(Message.Type.GET_MOVIE, String.valueOf(randomIndex)));
    Channel.printMetrics(indexReq);

    return searchWithIndex(indexReq);
  }

  public SplayNode[] getTopTenMovies() {
    return preferences.getTopN(10);
  }

  public SplayNode getTopMovie() {
    return preferences.getRoot();
  }
}
