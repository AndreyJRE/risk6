package com.unima.risk6.database.exceptions;

/**
 * The NotValidPasswordException class is a custom exception that extends the RuntimeException
 * class. It is thrown when a password is not valid.
 *
 * @author astoyano
 */
public class NotValidPasswordException extends RuntimeException {

  /**
   * Constructs a new NotValidPasswordException with the specified detail message.
   *
   * @param message The detail message
   */
  public NotValidPasswordException(String message) {
    super(message);
  }
}
