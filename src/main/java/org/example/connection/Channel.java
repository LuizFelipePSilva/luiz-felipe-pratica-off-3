package org.example.connection;

import org.example.huffman.HuffmanCompressor;
import org.example.huffman.HuffmanCompressor.HuffmanResult;
import org.example.huffman.HuffmanTree;

public class Channel {

  public static boolean silent = false;

  public static class TransmissionResult {
    public String original;
    public String compressed;
    public int originalBits;
    public int compressedBits;
    public double compressionRate;
    private HuffmanTree root;

    public TransmissionResult(String original, String compressed) {
      this(original, compressed, null);
    }

    public TransmissionResult(String original, String compressed, HuffmanTree root) {
      this.original = original;
      this.compressed = compressed;
      this.originalBits = original.length() * 8;
      this.compressedBits = compressed.length();
      this.compressionRate = (1.0 - (double) compressedBits / originalBits) * 100;
      this.root = root;
    }
  }

  public static TransmissionResult send(Message message) {
    String formatted = message.format();

    if (silent) {
      return new TransmissionResult(formatted, formatted);
    }

    byte[] data = formatted.getBytes(java.nio.charset.StandardCharsets.ISO_8859_1);

    HuffmanResult result = HuffmanCompressor.compress(data);
    String decompressed = HuffmanCompressor.decompress(result.encodedData, result.root);

    if (!decompressed.equals(formatted)) {
      throw new RuntimeException("Erro de transmissao: mensagem corrompida");
    }

    return new TransmissionResult(formatted, result.encodedData, result.root);
  }

  public static Message receive(TransmissionResult result) {
    String formatted = (result.root != null)
        ? HuffmanCompressor.decompress(result.compressed, result.root)
        : result.compressed; // modo silencioso: sem compressão real
    return Message.parse(formatted);
  }

  public static void printMetrics(TransmissionResult result) {
    if (silent)
      return;
    System.out.println("─── Transmissão Huffman ───────────────────");
    System.out.println("Original   : " + result.original);
    System.out.println("Tamanho    : " + result.originalBits + " bits (" + result.original.length() + " chars)");
    System.out.println("Comprimido : " + result.compressedBits + " bits");
    System.out.printf("Compressão : %.2f%% de redução%n", result.compressionRate);
    System.out.println("───────────────────────────────────────────");
  }
}
