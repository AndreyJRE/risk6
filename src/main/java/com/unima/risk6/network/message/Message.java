package com.unima.risk6.network.message;


public abstract class Message <MessageType> {

  private int statusCode;
  private MessageType content;
  public Message (MessageType content) {
    this.content = content;
    this.statusCode = -1;
  }

  public Message (MessageType content, int statusCode) {
    this.content = content;
    this.statusCode = statusCode;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(int statusCode) {
    this.statusCode = statusCode;
  }

  public MessageType getContent() {
    return content;
  }

  public void setContent(MessageType content) {
    this.content = content;
  }

  public boolean equals(Message obj) {
    return getContent().equals(obj.getContent()) && getStatusCode() == obj.getStatusCode();
  }
}
