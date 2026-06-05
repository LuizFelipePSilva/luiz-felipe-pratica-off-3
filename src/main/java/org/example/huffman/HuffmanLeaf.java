package org.example.huffman;

public class HuffmanLeaf extends HuffmanTree {
  public final byte value;

  public HuffmanLeaf(int freq, byte value) {
    super(freq);
    this.value = value;
  }
}
