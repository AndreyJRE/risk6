package com.unima.risk6.network.server.exceptions;


/**
 * An exception that is thrown when an invalid move is attempted in the server.
 *
 * @author wphung
 */
public class InvalidMoveException extends RuntimeException {

  /**
   * Constructs a new InvalidMoveException with the specified detail message.
   *
   * @param message the detail message
   */
  public InvalidMoveException(String message) {
    super(message);
  }

}
