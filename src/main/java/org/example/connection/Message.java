package org.example.connection;

public class Message {
  public enum Type {
    LOGIN_OK,
    GET_MOVIE,
    MOVIE_RESPONSE,
    RECOMMENDATION,
    CACHE_LOOKUP,
    CACHE_RESPONSE,
    HASH_LOOKUP,
    HASH_RESPONSE,
    SPLAY_INSERT
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
      case CACHE_LOOKUP -> "CACHE_GET:" + content;
      case CACHE_RESPONSE -> "CACHE_RESP:" + content;
      case HASH_LOOKUP -> "HASH_GET:" + content;
      case HASH_RESPONSE -> "HASH_RESP:" + content;
      case SPLAY_INSERT -> "SPLAY_INS:" + content;
    };
  }

  public static Message parse(String formatted) {
    if (formatted.equals("LOGIN_OK"))
      return new Message(Type.LOGIN_OK, null);
    if (formatted.startsWith("GET /filme/"))
      return new Message(Type.GET_MOVIE, formatted.substring("GET /filme/".length()));
    if (formatted.startsWith("FILME:"))
      return new Message(Type.MOVIE_RESPONSE, formatted.substring("FILME:".length()));
    if (formatted.startsWith("RECOMENDACAO:"))
      return new Message(Type.RECOMMENDATION, formatted.substring("RECOMENDACAO:".length()));
    if (formatted.startsWith("CACHE_GET:"))
      return new Message(Type.CACHE_LOOKUP, formatted.substring("CACHE_GET:".length()));
    if (formatted.startsWith("CACHE_RESP:"))
      return new Message(Type.CACHE_RESPONSE, formatted.substring("CACHE_RESP:".length()));
    if (formatted.startsWith("HASH_GET:"))
      return new Message(Type.HASH_LOOKUP, formatted.substring("HASH_GET:".length()));
    if (formatted.startsWith("HASH_RESP:"))
      return new Message(Type.HASH_RESPONSE, formatted.substring("HASH_RESP:".length()));
    if (formatted.startsWith("SPLAY_INS:"))
      return new Message(Type.SPLAY_INSERT, formatted.substring("SPLAY_INS:".length()));
    throw new IllegalArgumentException("Mensagem desconhecida: " + formatted);
  }

  public Type getType() {
    return type;
  }

  public String getContent() {
    return content;
  }
}
