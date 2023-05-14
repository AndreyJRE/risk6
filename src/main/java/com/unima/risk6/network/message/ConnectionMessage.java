package com.unima.risk6.network.message;

import com.unima.risk6.network.message.enums.ConnectionActions;
import com.unima.risk6.network.message.enums.ContentType;

/**
 * Message wrapper used for wrapping objects that are used to create and manipulate game lobbies,
 * server lobbies, etc.
 *
 * @param <T> the type of the content this message carries.
 * @author jferch
 */
public class ConnectionMessage<T> extends Message<T> {

  private final ConnectionActions connectionActions;

  /**
   * Constructs a ConnectionMessage object with given connection action and content.
   *
   * @param connectionActions the connection action this message represents.
   * @param content           the content of the message.
   */
  public ConnectionMessage(ConnectionActions connectionActions, T content) {
    super(content, ContentType.CONNECTION);
    this.connectionActions = connectionActions;
  }

  /**
   * Constructs a ConnectionMessage object with given connection action and content.
   *
   * @param connectionActions the connection action this message represents.
   * @param content           the content of the message.
   * @param statusCode        the status of the message.
   */
  public ConnectionMessage(ConnectionActions connectionActions, int statusCode, T content) {
    super(content, ContentType.CONNECTION, statusCode);
    this.connectionActions = connectionActions;
  }

  public ConnectionActions getConnectionActions() {
    return connectionActions;
  }
}