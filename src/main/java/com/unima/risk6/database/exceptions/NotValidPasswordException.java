package com.unima.risk6.database.exceptions;

public class NotValidPasswordException extends RuntimeException {

  public NotValidPasswordException(String message) {
    super(message);
  }
}
