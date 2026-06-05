package org.example.connection;

public class Message {
  public enum Type {
    LOGIN_OK,
    GET_MOVIE,
    MOVIE_RESPONSE,
    RECOMMENDATION
  }

  private Type type;
  private String content;

  public Message(Type type, String content) {
    this.type = type;
    this.content = content;
  }

  public String format() {
    return switch (type) {
      case LOGIN_OK -> "LOGIN_OK";
      case GET_MOVIE -> "GET /filme/" + content;
      case MOVIE_RESPONSE -> "FILME:" + content;
      case RECOMMENDATION -> "RECOMENDACAO:" + content;
    };
  }

  public Type getType() {
    return type;
  }

  public String getContent() {
    return content;
  }
}
