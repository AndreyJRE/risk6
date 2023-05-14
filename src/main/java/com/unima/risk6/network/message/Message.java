package com.unima.risk6.network.message;

import com.unima.risk6.game.logic.Attack;
import com.unima.risk6.game.logic.EndPhase;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.logic.HandIn;
import com.unima.risk6.game.logic.Reinforce;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.network.message.enums.ContentType;
import java.util.HashMap;

/**
 * Message is a abstract wrapper class for all objects that have to be transmitted between server
 * and client.
 *
 * @param <T> the type of the content this message carries.
 * @author jferch
 */
public abstract class Message<T> {

  private int statusCode;
  private T content;
  private ContentType contentType = ContentType.DEFAULT;

  /**
   * Constructs a Message object with given content. Status code is set to -1 by default.
   *
   * @param content the content of the message.
   */
  public Message(T content) {
    this(content, -1);
  }

  /**
   * Constructs a Message object with given content, content type, and status code.
   *
   * @param content     the content of the message.
   * @param contentType the type of the content.
   * @param statusCode  the status code for the message.
   */
  public Message(T content, ContentType contentType, int statusCode) {
    this(content, statusCode);
    this.contentType = contentType;
  }

  /**
   * Constructs a Message object with given content and content type. Status code is set to -1 by
   * default.
   *
   * @param content     the content of the message.
   * @param contentType the type of the content.
   */
  public Message(T content, ContentType contentType) {
    this(content);
    this.contentType = contentType;
  }

  /**
   * Constructs a Message object with given content and status code. The content type is determined
   * based on the class of the content.
   *
   * @param content    the content of the message.
   * @param statusCode the status code for the message.
   */
  public Message(T content, int statusCode) {
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
    } else if (content.getClass().equals(HashMap.class)) {
      contentType = ContentType.ORDER;
    }
  }

  public int getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(int statusCode) {
    this.statusCode = statusCode;
  }

  public T getContent() {
    return content;
  }

  public void setContent(T content) {
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