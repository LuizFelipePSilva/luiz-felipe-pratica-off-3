package org.example.simulation;

import org.example.client.Client;
import org.example.client.LRUList;
import org.example.client.SplayNode;
import org.example.connection.Channel;
import org.example.connection.Channel.TransmissionResult;
import org.example.connection.Message;
import org.example.movie.Category;
import org.example.movie.Movie;
import org.example.movie.MovieGenerator;
import org.example.server.Server;

import java.util.Scanner;

public class Simulation {
  Server server;
  Client[] clients;
  Movie[] itens;

  public Simulation() {
    this.server = new Server();
    this.clients = new Client[] {
        new Client(server),
        new Client(server),
        new Client(server)
    };
    Channel.silent = true;
    populate();
    fillCache();
    Channel.silent = false;
  }

  public void populate() {
    this.itens = MovieGenerator.generate(1000);
    for (Movie m : itens) {
      server.addMovie(m);
    }
  }

  public void fillCache() {
    for (int i = 0; i < 50; i++) {
      for (Client c : clients) {
        c.buscarParaCache(itens[i].getId());
      }
    }
  }

  private static void clearConsole() {
    for (int i = 0; i < 50; i++)
      System.out.println();
  }

  public void run() {
    clearConsole();
    System.out.println("╔══════════════════════════════════════╗");
    System.out.println("║        SIMULAÇÃO DE 20 CONSULTAS     ║");
    System.out.println("╚══════════════════════════════════════╝");

    for (int c = 0; c < clients.length; c++) {
      Channel.silent = (c != 0);

      if (!Channel.silent) {
        System.out.println("\n══════════════════════════════════════");
        System.out.println("  CLIENTE " + (c + 1));
        System.out.println("══════════════════════════════════════");

        System.out.println("\n▶ [1/4] CONSULTAS INVÁLIDAS");
        System.out.println("─────────────────────────────────────");
      }
      twoInvalid(clients[c]);

      if (!Channel.silent) {
        System.out.println("\n▶ [2/4] CONSULTAS NO CACHE — Hash Table + LRU");
        System.out.println("─────────────────────────────────────");
      }
      sixInCache(clients[c]);

      if (!Channel.silent) {
        System.out.println("\n▶ [3/4] CONSULTAS SEM INDEXAÇÃO — Lista Ligada");
        System.out.println("─────────────────────────────────────");
      }
      sixValidWithoutIndex(clients[c]);

      if (!Channel.silent) {
        System.out.println("\n▶ [4/4] CONSULTAS COM INDEXAÇÃO — Hash Table");
        System.out.println("─────────────────────────────────────");
      }
      sixValidWithIndex(clients[c]);

      if (!Channel.silent) {
        printClientSplayResults(clients[c], c + 1);
        printLRUResults(clients[c], c + 1);
      }
    }
    Channel.silent = false;

    printServerSplayResults();
    printHuffmanDemo();

    System.out.println("\n╔══════════════════════════════════════╗");
    System.out.println("║              ANÁLISE                 ║");
    System.out.println("╚══════════════════════════════════════╝");
    System.out.println("  Hash + LRU (cache) → O(1) busca. Sem acesso ao servidor.");
    System.out.println("  Hash    (índice)   → O(1) ~ 1 comparação. Acesso direto ao nó.");
    System.out.println("  Lista   (linear)   → O(n) ~ até 1000 comparações. Varredura sequencial.");
    System.out.println("\n  Conclusão: o índice hash elimina o custo linear da lista ligada.");
    System.out.println("             O cache LRU evita requisições desnecessárias ao servidor.");
  }

  private void twoInvalid(Client client) {
    client.buscar(1001);
    if (!Channel.silent) {
      System.out.println("  ✘ ID 1001 → não encontrado");
      if (client.lastTransmission != null)
        Channel.printMetrics(client.lastTransmission);
    }

    client.buscar(1002);
    if (!Channel.silent) {
      System.out.println("  ✘ ID 1002 → não encontrado");
      if (client.lastTransmission != null)
        Channel.printMetrics(client.lastTransmission);
    }
  }

  private void sixInCache(Client client) {
    for (int i = 0; i < 6; i++) {
      Movie a = client.buscar(itens[i].getId());
      if (a != null && !Channel.silent) {
        System.out.println("  ✔ " + a.toStringMinor() + " | cache hit | comparações: 1");
        // Cache hit: sem transmissão ao servidor, exibe demo de LOGIN_OK
        TransmissionResult tr = Channel.send(new Message(Message.Type.LOGIN_OK, null));
        Channel.printMetrics(tr);
      }
    }
  }

  private void sixValidWithIndex(Client client) {
    for (int i = 994; i < 1000; i++) {
      Movie a = client.buscar(itens[i].getId());
      if (a != null && !Channel.silent) {
        System.out.println("  ✔ " + a.toStringMinor() + " | comparações hash: " + server.lastComparisonCount);
        Channel.printMetrics(client.lastTransmission);
      }
    }
  }

  private void sixValidWithoutIndex(Client client) {
    for (int i = 994; i < 1000; i++) {
      Movie a = client.buscarSemIndice(itens[i].getId());
      if (a != null && !Channel.silent) {
        System.out.println("  ✔ " + a.toStringMinor() + " | comparações lista: " + server.lastComparisonCount);
        Channel.printMetrics(client.lastTransmission);
      }
    }
  }

  private void printClientSplayResults(Client client, int clientNum) {
    System.out.println("\n╔══════════════════════════════════════╗");
    System.out.println("║  SPLAY PREFERÊNCIAS — CLIENTE " + clientNum + "      ║");
    System.out.println("╚══════════════════════════════════════╝");
    SplayNode root = client.getPreferenceRoot();
    if (root != null)
      System.out.println("  Categoria mais relevante (raiz): " + root.value);
    else
      System.out.println("  Splay vazia.");

    System.out.println("  Top 5 categorias acessadas:");
    SplayNode[] top5 = client.getTopFiveCategories();
    for (int i = 0; i < top5.length; i++) {
      if (top5[i] != null)
        System.out.println("    " + (i + 1) + ". " + top5[i].value);
    }
  }

  private void printLRUResults(Client client, int clientNum) {
    System.out.println("\n╔══════════════════════════════════════╗");
    System.out.println("║  CACHE LRU — CLIENTE " + clientNum + "               ║");
    System.out.println("╚══════════════════════════════════════╝");
    System.out.println("  10 filmes mais recentemente utilizados:");
    Movie[] recent = client.cacheManager.getRecentTen();
    for (int i = 0; i < recent.length; i++) {
      if (recent[i] != null)
        System.out.println("    " + (i + 1) + ". " + recent[i].toStringMinor());
    }

    LRUList evictedList = client.cacheManager.getEvictedList();
    System.out.println("  Removidos por eviction LRU: " + evictedList.getSize());
    LRUList.LRUNode ev = evictedList.getHead();
    while (ev != null) {
      System.out.println("    ✘ " + ev.movie.toStringMinor());
      ev = ev.next;
    }
  }

  private void printServerSplayResults() {
    System.out.println("\n╔══════════════════════════════════════╗");
    System.out.println("║   SPLAY POPULARIDADE — SERVIDOR      ║");
    System.out.println("╚══════════════════════════════════════╝");
    SplayNode top = server.getTopMovie();
    if (top != null)
      System.out.println("  Filme mais popular globalmente (raiz): " + top.value);
    else
      System.out.println("  Splay vazia.");

    System.out.println("  Top 10 filmes mais próximos da raiz:");
    SplayNode[] top10 = server.getTopTenMovies();
    for (int i = 0; i < top10.length; i++) {
      if (top10[i] != null)
        System.out.println("    " + (i + 1) + ". " + top10[i].value);
    }
  }

  private void printHuffmanDemo() {
    System.out.println("\n╔══════════════════════════════════════╗");
    System.out.println("║       COMPRESSÃO HUFFMAN             ║");
    System.out.println("╚══════════════════════════════════════╝");
    System.out.println("  Exemplos de mensagens transmitidas:");

    Message[] msgs = new Message[] {
        new Message(Message.Type.LOGIN_OK, null),
        new Message(Message.Type.GET_MOVIE, "505"),
        new Message(Message.Type.MOVIE_RESPONSE, "505|Matrix|1999"),
        new Message(Message.Type.RECOMMENDATION, "202|Interestelar")
    };

    for (Message msg : msgs) {
      TransmissionResult tr = Channel.send(msg);
      Channel.printMetrics(tr);
    }
  }

  public void buscarPorCategoria(Scanner scanner) {
    clearConsole();
    System.out.println("╔══════════════════════════════════════╗");
    System.out.println("║          BUSCA POR CATEGORIA         ║");
    System.out.println("╚══════════════════════════════════════╝");
    int i = 0;
    for (Category c : Category.values()) {
      i++;
      System.out.println("  " + i + ". " + c.name());
    }
    System.out.print("\nDigite a categoria: ");
    String lido = scanner.nextLine();

    Category categoriaEscolhida;
    try {
      categoriaEscolhida = Category.valueOf(lido.toUpperCase());
    } catch (IllegalArgumentException e) {
      System.out.println("  ✘ Categoria não encontrada.");
      return;
    }

    Movie[] filtrados = java.util.Arrays.stream(itens)
        .filter(m -> m.getCategory() == categoriaEscolhida)
        .toArray(Movie[]::new);

    int pagina = 0;
    int porPagina = 10;
    String opcao = "";

    while (!opcao.equals("0")) {
      clearConsole();
      int inicio = pagina * porPagina;
      int fim = Math.min(inicio + porPagina, filtrados.length);

      System.out.println("\n  Filmes em " + categoriaEscolhida.name() + " — Página " + (pagina + 1));
      System.out.println("  ─────────────────────────────────────");
      for (int j = inicio; j < fim; j++) {
        System.out.println("  " + filtrados[j].toStringMinor());
      }

      System.out.println("\n  [n] próxima  [p] anterior  [0] voltar");
      System.out.print("  Ou digite o nome do filme: ");
      opcao = scanner.nextLine().trim();

      if (opcao.equals("n") && fim < filtrados.length)
        pagina++;
      else if (opcao.equals("p") && pagina > 0)
        pagina--;
      else if (!opcao.equals("0") && !opcao.isEmpty()) {
        int idEncontrado = -1;
        for (int j = inicio; j < fim; j++) {
          if (filtrados[j].getTitle().equalsIgnoreCase(opcao)) {
            idEncontrado = filtrados[j].getId();
            break;
          }
        }
        if (idEncontrado != -1) {
          buscar(idEncontrado, scanner);
        } else {
          System.out.println("  ✘ Filme não encontrado nesta página.");
          System.out.print("  [Enter] para continuar...");
          scanner.nextLine();
        }
      }
    }
  }

  public void buscarPorNome(Scanner scanner) {
    int pagina = 0;
    int porPagina = 10;
    String opcao = "";

    while (!opcao.equals("0")) {
      clearConsole();
      int inicio = pagina * porPagina;
      int fim = Math.min(inicio + porPagina, itens.length);

      System.out.println("\n  Página " + (pagina + 1));
      System.out.println("  ─────────────────────────────────────");
      for (int i = inicio; i < fim; i++) {
        System.out.println("  " + itens[i].toStringMinor());
      }

      System.out.println("\n  [n] próxima  [p] anterior  [0] voltar");
      System.out.print("  Ou digite o nome do filme: ");
      opcao = scanner.nextLine().trim();

      if (opcao.equals("n") && fim < itens.length)
        pagina++;
      else if (opcao.equals("p") && pagina > 0)
        pagina--;
      else if (!opcao.equals("0") && !opcao.isEmpty()) {
        int idEncontrado = -1;
        for (int i = inicio; i < fim; i++) {
          if (itens[i].getTitle().equalsIgnoreCase(opcao)) {
            idEncontrado = itens[i].getId();
            break;
          }
        }
        if (idEncontrado != -1) {
          buscar(idEncontrado, scanner);
        } else {
          System.out.println("  ✘ Filme não encontrado nesta página.");
          System.out.print("  [Enter] para continuar...");
          scanner.nextLine();
        }
      }
    }
  }

  private void buscar(int id, Scanner scanner) {
    clearConsole();

    Movie filme = clients[0].buscar(id);

    if (filme == null) {
      System.out.println("  ✘ Nenhum filme encontrado para o ID " + id);
      System.out.print("\n  [Enter] para voltar...");
      scanner.nextLine();
      return;
    }

    System.out.println("\n  ✔ Filme encontrado:");
    System.out.println("  " + filme);

    if (clients[0].lastTransmission != null) {
      Channel.printMetrics(clients[0].lastTransmission);
    }

    Movie recomendacao = clients[0].getRecomendation();

    if (recomendacao != null) {
      System.out.println("\n  Recomendado para você:");
      System.out.println("  " + recomendacao);

      if (clients[0].lastTransmission != null) {
        Channel.printMetrics(clients[0].lastTransmission);
      }
    }

    System.out.print("\n  [Enter] para voltar...");
    scanner.nextLine();
  }
}
