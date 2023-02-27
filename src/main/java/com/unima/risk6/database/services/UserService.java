package com.unima.risk6.database.services;

import com.unima.risk6.database.repositories.UserRepository;

public class UserService {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }


}
