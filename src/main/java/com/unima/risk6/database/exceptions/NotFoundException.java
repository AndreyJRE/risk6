package com.unima.risk6.database.exceptions;


/**
 * The NotFoundException class is a custom exception that extends the RuntimeException class. It is
 * thrown when a resource is not found.
 *
 * @author astoyano
 */
public class NotFoundException extends RuntimeException {

  /**
   * Constructs a new NotFoundException with the specified detail message.
   *
   * @param message The detail message.
   */
  public NotFoundException(String message) {
    super(message);
  }
}
