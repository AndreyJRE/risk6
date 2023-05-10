package com.unima.risk6.network.message;

import com.unima.risk6.network.message.enums.ContentType;

public class ChatMessage extends Message {

  public ChatMessage(String content) {
    super(content, ContentType.CHAT_MESSAGE);

  }

  public ChatMessage(String content, int statusCode) {
    super(content, ContentType.CHAT_MESSAGE, statusCode);
  }
}
