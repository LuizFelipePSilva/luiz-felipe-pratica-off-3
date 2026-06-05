package org.example.huffman;

class HuffmanNode extends HuffmanTree {
  public final HuffmanTree left, right;

  HuffmanNode(HuffmanTree left, HuffmanTree right) {
    super(left.frequency + right.frequency);
    this.left = left;
    this.right = right;
  }
}
