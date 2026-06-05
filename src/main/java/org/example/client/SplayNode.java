package org.example.client;

public class SplayNode {
  public int key;
  public String value;
  public SplayNode left, right, parent;

  public SplayNode(int key, String value) {
    this.key = key;
    this.value = value;
  }
}
