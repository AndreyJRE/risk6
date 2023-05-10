package com.unima.risk6.network.message;

import com.unima.risk6.game.logic.Attack;
import com.unima.risk6.game.logic.EndPhase;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.logic.HandIn;
import com.unima.risk6.game.logic.Reinforce;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.network.message.enums.ContentType;

public abstract class Message<MessageType> {

  private int statusCode;
  private MessageType content;
  private ContentType contentType = ContentType.DEFAULT;

  public Message(MessageType content) {
    this(content, -1);
  }

  public Message(MessageType content, ContentType contentType, int statusCode) {
    this(content, statusCode);
    this.contentType = contentType;
  }

  public Message(MessageType content, ContentType contentType) {
    this(content);
    this.contentType = contentType;
  }

  public Message(MessageType content, int statusCode) {
    this.content = content;
    this.statusCode = statusCode;
    if (content.getClass().equals(Attack.class)) {
      contentType = ContentType.ATTACK;
    } else if (content.getClass().equals(Fortify.class)) {
      contentType = ContentType.FORTIFY;
    } else if (content.getClass().equals(GameState.class)) {
      contentType = ContentType.GAME_STATE;
    } else if (content.getClass().equals(Reinforce.class)) {
      contentType = ContentType.REINFORCE;
    } else if (content.getClass().equals(HandIn.class)) {
      contentType = ContentType.HAND_IN;
    } else if (content.getClass().equals(EndPhase.class)) {
      contentType = ContentType.END_PHASE;
    }
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

  public ContentType getContentType() {
    return contentType;
  }

  public void setContentType(ContentType contentType) {
    this.contentType = contentType;
  }

  public boolean equals(Message obj) {
    return getContent().equals(obj.getContent()) && getStatusCode() == obj.getStatusCode();
  }
}