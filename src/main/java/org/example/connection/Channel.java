package org.example.connection;

import org.example.huffman.HuffmanCompressor;
import org.example.huffman.HuffmanCompressor.HuffmanResult;

public class Channel {

  public static class TransmissionResult {
    public String original;
    public String compressed;
    public int originalBits;
    public int compressedBits;
    public double compressionRate;

    public TransmissionResult(String original, String compressed) {
      this.original = original;
      this.compressed = compressed;
      this.originalBits = original.length() * 8;
      this.compressedBits = compressed.length();
      this.compressionRate = (1.0 - (double) compressedBits / originalBits) * 100;
    }
  }

  public static TransmissionResult send(Message message) {
    String formatted = message.format();
    byte[] data = formatted.getBytes();

    HuffmanResult result = HuffmanCompressor.compress(data);
    String decompressed = HuffmanCompressor.decompress(result.encodedData, result.root);

    if (!decompressed.equals(formatted)) {
      throw new RuntimeException("Erro de transmissao: mensagem corrompida");
    }

    return new TransmissionResult(formatted, result.encodedData);
  }

  public static void printMetrics(TransmissionResult result) {
    System.out.println("─── Transmissão Huffman ───────────────────");
    System.out.println("Original   : " + result.original);
    System.out.println("Tamanho    : " + result.originalBits + " bits (" + result.original.length() + " chars)");
    System.out.println("Comprimido : " + result.compressedBits + " bits");
    System.out.printf("Compressão : %.2f%% de redução%n", result.compressionRate);
    System.out.println("───────────────────────────────────────────");
  }
}
