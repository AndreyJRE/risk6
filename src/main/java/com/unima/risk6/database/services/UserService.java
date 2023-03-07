package com.unima.risk6.database.services;

import com.unima.risk6.database.exceptions.NotFoundException;
import com.unima.risk6.database.models.GameStatistic;
import com.unima.risk6.database.models.User;
import com.unima.risk6.database.repositories.UserRepository;
import java.util.List;

public class UserService {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }


  public User getUserById(Long userId) {
    return userRepository.get(userId).orElseThrow(
        () -> new NotFoundException("User with id {" + userId + "} is not in the database"));
  }

  public List<User> getAllUsers() {
    return userRepository.getAll();
  }

  public void saveUser(User user) {
    //TODO Validate username and password, encryption for password is also needed
    userRepository.save(user);
  }

  public void deleteUserById(Long userId) {
    userRepository.deleteById(userId);
  }

  public void updateUser(User user) {
    //TODO Validate username and password, encryption for password is also needed
    userRepository.update(user);
  }

  public List<GameStatistic> getAllStatisticsByUserId(Long userId) {
    return userRepository.getAllStatisticsByUserId(userId);
  }


}
