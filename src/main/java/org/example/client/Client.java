package org.example.client;

import org.example.connection.Channel;
import org.example.connection.Channel.TransmissionResult;
import org.example.connection.Message;
import org.example.movie.Movie;
import org.example.movie.Category;
import org.example.server.Server;

public class Client {
  public CacheManager cacheManager;
  public Server server;
  public SplayTree preferences;
  public TransmissionResult lastTransmission;

  public Client(Server server) {
    cacheManager = new CacheManager();
    this.server = server;
    preferences = new SplayTree();
  }

  public Movie buscar(int id) {
    lastTransmission = null;
    Movie temp = cacheManager.search(id);

    if (temp == null) {
      Message request = new Message(Message.Type.GET_MOVIE, String.valueOf(id));
      lastTransmission = Channel.send(request);

      temp = server.searchWithIndex(id);

      if (temp != null) {
        Message response = new Message(Message.Type.MOVIE_RESPONSE,
            temp.getId() + "|" + temp.getTitle());
        lastTransmission = Channel.send(response);
        cacheManager.add(temp);
      }
    }

    if (temp != null) {
      preferences.insert(temp.getCategory().ordinal(), temp.getCategory().name());
    }

    return temp;
  }

  public void buscarParaCache(int id) {
    Movie temp = server.searchWithIndex(id);
    if (temp != null)
      cacheManager.add(temp);
  }

  public Movie buscarSemIndice(int id) {
    Message request = new Message(Message.Type.GET_MOVIE, String.valueOf(id));
    lastTransmission = Channel.send(request);

    Movie temp = server.searchWithoutIndex(id);

    if (temp != null) {
      Message response = new Message(Message.Type.MOVIE_RESPONSE,
          temp.getId() + "|" + temp.getTitle());
      lastTransmission = Channel.send(response);
    }

    return temp;
  }

  public SplayNode getPreferenceRoot() {
    return preferences.getRoot();
  }

  public SplayNode[] getTopFiveCategories() {
    return preferences.getTopN(5);
  }

  public Movie getRecomendation() {
    if (preferences.getRoot() == null)
      return null;

    String favoriteCategory = preferences.getRoot().value;

    Message request = new Message(Message.Type.RECOMMENDATION, favoriteCategory);

    lastTransmission = Channel.send(request);

    Category category = Category.valueOf(favoriteCategory);

    Movie temp = server.searchByCategory(category);

    if (temp != null) {
      lastTransmission = Channel.send(
          new Message(
              Message.Type.MOVIE_RESPONSE,
              temp.getId() + "|" + temp.getTitle()));
    }

    return temp;
  }
}
