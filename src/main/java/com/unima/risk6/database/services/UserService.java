package com.unima.risk6.database.services;

import com.unima.risk6.database.configurations.PasswordEncryption;
import com.unima.risk6.database.exceptions.NotFoundException;
import com.unima.risk6.database.exceptions.UsernameNotUniqueException;
import com.unima.risk6.database.models.User;
import com.unima.risk6.database.repositories.UserRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


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

  public Long saveUser(User user) {
    //TODO Check also if password and username valid are, like number of letters (RegEx)
    if (user.getUsername() == null || user.getUsername().isEmpty() || user.getPassword() == null
        || user.getPassword().isEmpty()) {
      throw new IllegalArgumentException("Username and password must not be empty");
    }
    // Check if the username is unique
    Optional<User> existingUser = userRepository.getUserByUsername(user.getUsername());
    if (existingUser.isPresent()) {
      throw new UsernameNotUniqueException("Username is already taken");
    }
    // Encrypt password
    String encryptedPassword = PasswordEncryption.encryptPassword(user.getPassword());
    user.setPassword(encryptedPassword);
    user.setCreatedAt(LocalDate.now());
    user.setActive(true);
    return userRepository.save(user);
  }

  public void deleteUserById(Long userId) {
    Optional<User> databaseUser = userRepository.get(userId);
    if (databaseUser.isEmpty()) {
      throw new NotFoundException("User with id {" + userId + "} is not in the database");
    }
    userRepository.deleteById(userId);
  }

  public void updateUser(User user) {
    //TODO Check if username is new, then check for unique and update
    //TODO Check also if password and username valid are, like number of letters (RegEx)
    if (user.getUsername() == null || user.getUsername().isEmpty() || user.getPassword() == null
        || user.getPassword().isEmpty()) {
      throw new IllegalArgumentException("Username and password must not be empty");
    }
    User userDatabase = getUserById(user.getId());
    if (!userDatabase.getPassword().equals(user.getPassword())) {
      String encryptedPassword = PasswordEncryption.encryptPassword(user.getPassword());
      user.setPassword(encryptedPassword);
    }

    userRepository.update(user);
  }


  public User getUserByUsername(String username) {
    return userRepository.getUserByUsername(username)
        .orElseThrow(() -> new NotFoundException(
            "User with username {" + username + "} is not in the database"));
  }


}
