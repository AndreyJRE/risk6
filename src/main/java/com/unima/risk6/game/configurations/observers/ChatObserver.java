package com.unima.risk6.game.configurations.observers;

import java.util.List;

/**
 * The ChatObserver defines a method that allows an observer to update the chat when the client
 * receives a new chat message.
 *
 * @author jferch
 */
public interface ChatObserver {

  /**
   * Updates the chat based on the provided list of strings.
   *
   * @param string The list of strings used to update the chat.
   */
  public void updateChat(List<String> string);

}