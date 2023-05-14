package com.unima.risk6.network.message;

import com.unima.risk6.network.message.enums.ContentType;

/**
 * Message wrapper used for wrapping chat message objects.
 *
 * @author jferch
 */

public class ChatMessage extends Message {

  /**
   * Constructs a ChatMessage object with the given message.
   *
   * @param content the message.
   */
  public ChatMessage(String content) {
    super(content, ContentType.CHAT_MESSAGE);

  }
}
