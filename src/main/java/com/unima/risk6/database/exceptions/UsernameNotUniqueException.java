package com.unima.risk6.database.exceptions;

/**
 * The UsernameNotUniqueException class is a custom exception that extends the RuntimeException
 * class. It is thrown when a username is not unique.
 *
 * @author astoyano
 */
public class UsernameNotUniqueException extends RuntimeException {

  /**
   * Constructs a new UsernameNotUniqueException with the specified detail message.
   *
   * @param message The detail message.
   */
  public UsernameNotUniqueException(String message) {
    super(message);
  }

}