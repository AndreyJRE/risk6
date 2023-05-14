package com.unima.risk6.network.server.exceptions;

/**
 * Exception for when a move is invalid.
 *
 * @author wphung
 */
public class InvalidMoveException extends RuntimeException {

  /**
   * Constructor for the exception.
   *
   * @param message The message to be displayed.
   */

  public InvalidMoveException(String message) {
    super(message);
  }

}
