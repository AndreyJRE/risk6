package com.unima.risk6.network.message;

import com.unima.risk6.network.message.enums.ConnectionActions;
import com.unima.risk6.network.message.enums.ContentType;

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

  public ConnectionActions getConnectionActions() {
    return connectionActions;
  }
}