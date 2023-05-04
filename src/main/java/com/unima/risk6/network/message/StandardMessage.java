package com.unima.risk6.network.message;

public class StandardMessage<MessageType> extends Message<MessageType> {

  public StandardMessage(MessageType content, int status) {
    super(content, status);
  }

  public StandardMessage(MessageType content) {
    super(content);
  }


}
