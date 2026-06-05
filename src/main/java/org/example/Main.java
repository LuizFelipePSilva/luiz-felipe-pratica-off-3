package org.example;

import org.example.simulation.Simulation;

import java.util.Scanner;

public class Main {
  public static void main(String[] args) {
    Simulation simulation = new Simulation();

    System.out.println("=== Sistema de Streaming ===");
    System.out.println("Servidor: 1000 filmes carregados.");
    System.out.println("Cache: 50 filmes pré-carregados.");
    Scanner scanner = new Scanner(System.in);
    int opcao = -1;

    while (opcao != 0) {
      System.out.println("\n1 - Buscar por categoria");
      System.out.println("2 - Buscar por nome");
      System.out.println("3 - Executar 20 consultas");
      System.out.println("0 - Sair");
      opcao = Integer.parseInt(scanner.nextLine());

      switch (opcao) {
        case 1 -> simulation.buscarPorCategoria(scanner);
        case 2 -> simulation.buscarPorNome(scanner);
        case 3 -> simulation.run();
      }
    }
  }
}
