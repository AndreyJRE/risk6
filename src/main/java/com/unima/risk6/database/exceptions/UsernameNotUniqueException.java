package com.unima.risk6.database.exceptions;

public class UsernameNotUniqueException extends RuntimeException {

  public UsernameNotUniqueException(String message) {
    super(message);
  }

}