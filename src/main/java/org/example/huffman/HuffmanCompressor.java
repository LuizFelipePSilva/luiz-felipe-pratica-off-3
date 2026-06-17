package org.example.huffman;

public class HuffmanCompressor {

  public static HuffmanResult compress(byte[] data) {
    int[] frequency = new int[256];
    for (byte b : data) {
      frequency[b & 0xFF]++;
    }

    HuffmanTree root = buildTree(frequency);

    HuffmanHashTable localCodeTable = new HuffmanHashTable();
    generateCodes(root, "", localCodeTable);

    StringBuilder encodedString = new StringBuilder();
    for (byte b : data) {
      encodedString.append(localCodeTable.get(b));
    }

    return new HuffmanResult(encodedString.toString(), root);
  }

  private static HuffmanTree buildTree(int[] charFrequency) {
    MinHeap<HuffmanTree> trees = new MinHeap<>();

    for (int i = 0; i < charFrequency.length; i++) {
      if (charFrequency[i] > 0) {
        trees.add(new HuffmanLeaf(charFrequency[i], (byte) i));
      }
    }

    if (trees.size() == 1) {
      HuffmanTree only = trees.poll();
      return new HuffmanNode(only, only);
    }

    while (trees.size() > 1) {
      HuffmanTree a = trees.poll();
      HuffmanTree b = trees.poll();
      trees.add(new HuffmanNode(a, b));
    }

    return trees.poll();
  }

  private static void generateCodes(HuffmanTree node, String code, HuffmanHashTable map) {
    if (node instanceof HuffmanLeaf) {
      HuffmanLeaf leaf = (HuffmanLeaf) node;
      map.put(leaf.value, code);
    } else if (node instanceof HuffmanNode) {
      HuffmanNode innerNode = (HuffmanNode) node;
      generateCodes(innerNode.left, code + "0", map);
      generateCodes(innerNode.right, code + "1", map);
    }
  }

  public static class HuffmanResult {
    public String encodedData;
    public HuffmanTree root;

    public HuffmanResult(String encodedData, HuffmanTree root) {
      this.encodedData = encodedData;
      this.root = root;
    }
  }

  public static String decompress(String encodedData, HuffmanTree root) {
    StringBuilder sb = new StringBuilder();
    HuffmanTree current = root;
    for (char bit : encodedData.toCharArray()) {
      current = (bit == '0')
          ? ((HuffmanNode) current).left
          : ((HuffmanNode) current).right;
      if (current instanceof HuffmanLeaf) {
        sb.append((char) (((HuffmanLeaf) current).value & 0xFF));
        current = root;
      }
    }
    return sb.toString();
  }
}
