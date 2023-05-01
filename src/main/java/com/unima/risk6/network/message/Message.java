package com.unima.risk6.network.message;

import com.unima.risk6.game.logic.Attack;
import com.unima.risk6.game.logic.Dice;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.logic.Move;
import com.unima.risk6.game.logic.Reinforce;

public abstract class Message <MessageType> {

  private int statusCode;
  private MessageType content;
  private ContentType contentType = ContentType.DEFAULT;

  public Message (MessageType content) {
    this(content,-1);
  }

  public Message (MessageType content, int statusCode) {
    this.content = content;
    this.statusCode = statusCode;
    if(content.getClass().equals(Attack.class)) {
      contentType = ContentType.ATTACK;
    }else if(content.getClass().equals(Fortify.class)) {
      contentType = ContentType.FORTIFY;
    }else if(content.getClass().equals(GameState.class)) {
      contentType = ContentType.GAMESTATE;
    }else if(content.getClass().equals(Reinforce.class)) {
      contentType = ContentType.REINFORCE;
    }else if(content.getClass().equals(ConnectionActions.class)) {
      contentType = ContentType.CONNECTION;
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
