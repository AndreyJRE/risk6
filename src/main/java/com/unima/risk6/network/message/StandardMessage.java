package com.unima.risk6.network.message;

/**
 * Message wrapper for most objects like: gamestate and move objects.
 *
 * @param <T> the type of the content this message carries.
 * @author jferch
 */
public class StandardMessage<T> extends Message<T> {

  /**
   * Constructs a Message object with given content and status code. The content type is determined
   * based on the class of the content.
   *
   * @param content the content of the message.
   * @param status  the status code for the message.
   */
  public StandardMessage(T content, int status) {
    super(content, status);
  }

  /**
   * Constructs a StandardMessage object with given content. Status code is set to -1 by default.
   *
   * @param content the content of the message.
   */
  public StandardMessage(T content) {
    super(content);
  }


}
