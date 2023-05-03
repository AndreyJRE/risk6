package com.unima.risk6.network.message;

public class ConnectionMessage<T> extends Message<T> {

  private final ConnectionActions connectionActions;

  public ConnectionMessage(ConnectionActions connectionActions, T content) {
    super(content, ContentType.CONNECTION);
    this.connectionActions = connectionActions;
  }

  public ConnectionMessage(ConnectionActions connectionActions, int statusCode, T content) {
    super(content, ContentType.CONNECTION, statusCode);
    this.connectionActions = connectionActions;
  }
}