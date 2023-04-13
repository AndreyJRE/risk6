package com.unima.risk6.database.services;

import com.unima.risk6.database.configurations.PasswordEncryption;
import com.unima.risk6.database.exceptions.NotFoundException;
import com.unima.risk6.database.exceptions.NotValidPasswordException;
import com.unima.risk6.database.exceptions.UsernameNotUniqueException;
import com.unima.risk6.database.models.User;
import com.unima.risk6.database.repositories.UserRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public class UserService {

  private final UserRepository userRepository;
  private final static String PASSWORD_EXCEPTION_MESSAGE = """
      Password should contain:
      at least one uppercase letter (A-Z)
      at least one lowercase letter (a-z)
      at least one digit (0-9)
      at least one special character (#?!@$%^&*-)
      and has a minimum length of 8 characters""";

  private static final String PASSWORD_REGEX = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?="
      + ".*?[#?!@$%^&*-]).{8,}$";

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
    if (user.getUsername() == null || user.getUsername().isEmpty() || user.getPassword() == null
        || user.getPassword().isEmpty()) {
      throw new IllegalArgumentException("Username and password must not be empty");
    }
    // Check if the username is unique
    Optional<User> existingUser = userRepository.getUserByUsername(user.getUsername());
    if (existingUser.isPresent()) {
      throw new UsernameNotUniqueException("Username is already taken");
    }
    if (!isPasswordValid(user.getPassword())) {
      throw new NotValidPasswordException(PASSWORD_EXCEPTION_MESSAGE);
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
    if (user.getUsername() == null || user.getUsername().isEmpty() || user.getPassword() == null
        || user.getPassword().isEmpty()) {
      throw new IllegalArgumentException("Username and password must not be empty");
    }
    User userDatabase = getUserById(user.getId());
    if (!userDatabase.getUsername().equals(user.getUsername())) {
      Optional<User> userWithUsername = userRepository.getUserByUsername(user.getUsername());
      if (userWithUsername.isPresent()) {
        throw new UsernameNotUniqueException(
            "This username{" + user.getUsername() + "} is already used");
      }
    }
    if (!userDatabase.getPassword().equals(user.getPassword())) {
      if (!isPasswordValid(user.getPassword())) {
        throw new NotValidPasswordException(PASSWORD_EXCEPTION_MESSAGE);
      }
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

  public boolean isPasswordValid(String password) {
    return password.matches(PASSWORD_REGEX);
  }

  public List<User> getUsersByActive(boolean active) {
    return userRepository.getAllUsersByActive(active);
  }

}
