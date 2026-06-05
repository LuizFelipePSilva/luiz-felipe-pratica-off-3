package org.example.huffman;

abstract class HuffmanTree implements Comparable<HuffmanTree> {
  public final int frequency;

  HuffmanTree(int frequency) {
    this.frequency = frequency;
  }

  @Override
  public int compareTo(HuffmanTree huffmanTree) {
    return frequency - huffmanTree.frequency;
  }
}
