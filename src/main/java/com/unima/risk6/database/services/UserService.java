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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The UserService class is a service class for the User model. It contains methods that are used to
 * retrieve, update, delete and save User objects. It uses the UserRepository class to communicate
 * with the database.
 *
 * @author astoyano
 */
public class UserService {

  private final UserRepository userRepository;
  private static final String PASSWORD_EXCEPTION_MESSAGE = """
      Password should contain:
      at least one uppercase letter (A-Z)
      at least one lowercase letter (a-z)
      at least one digit (0-9)
      at least one special character (#?!@$%^&*-)
      and has a minimum length of 8 characters""";
  private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
  private static final String PASSWORD_REGEX = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?="
      + ".*?[#?!@$%^&*-]).{8,}$";

  /**
   * Constructs a new UserService object with the specified UserRepository.
   *
   * @param userRepository The UserRepository to use for communication with the database
   */
  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * Retrieves a User object with the specified ID. If no User object is found, a NotFoundException
   * is thrown.
   *
   * @param userId The ID of the User object to retrieve
   * @return The User object with the specified ID
   * @throws NotFoundException If no User object is found with the specified ID
   */
  public User getUserById(Long userId) {
    return userRepository.get(userId).orElseThrow(
        () -> new NotFoundException("User with id {" + userId + "} is not in the database"));
  }

  /**
   * Retrieves all users from the database.
   *
   * @return A list of all users
   */
  public List<User> getAllUsers() {
    return userRepository.getAll();
  }

  /**
   * Saves a new user in the database. If the username is not unique, a UsernameNotUniqueException
   * is thrown. If the password is not valid, a NotValidPasswordException is thrown.
   *
   * @param user The user to save
   * @throws UsernameNotUniqueException If the username is not unique in the database
   * @throws NotValidPasswordException  If the password is not valid (does not match the password
   *                                    regex)
   */
  public void saveUser(User user) {
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
    userRepository.save(user);
    LOGGER.info("User with username {" + user.getUsername() + "} was saved in the database");
  }

  /**
   * Deletes a user with the specified ID from the database. If no user is found with the specified
   * ID, a NotFoundException is thrown.
   *
   * @param userId The ID of the user to delete
   * @throws NotFoundException If no user is found with the specified ID
   */
  public void deleteUserById(Long userId) {
    Optional<User> databaseUser = userRepository.get(userId);
    if (databaseUser.isEmpty()) {
      throw new NotFoundException("User with id {" + userId + "} is not in the database");
    }
    LOGGER.info("User with id {" + userId + "} was deleted from the database");
    userRepository.deleteById(userId);
  }

  /**
   * Updates a user in the database. If the username is not unique, a UsernameNotUniqueException is
   * thrown. If the password is not valid, a NotValidPasswordException is thrown.
   *
   * @param user The user to update in the database
   * @throws UsernameNotUniqueException If the username is not unique in the database
   * @throws NotValidPasswordException  If the password is not valid (does not match the password
   *                                    regex)
   */
  public void updateUser(User user) {
    if (user.getUsername() == null || user.getUsername().isEmpty() || user.getPassword() == null
        || user.getPassword().isEmpty()) {
      throw new IllegalArgumentException("Username and password must not be empty");
    }
    User userDatabase = getUserById(user.getId());
    if (!userDatabase.getUsername().equals(user.getUsername())) {
      Optional<User> userWithUsername = userRepository.getUserByUsername(user.getUsername());
      if (userWithUsername.isPresent()) {
        String newUsername = user.getUsername();
        user.setUsername(userDatabase.getUsername());
        throw new UsernameNotUniqueException(
            "This username{" + newUsername + "} is already used");

      }
    }
    if (!userDatabase.getPassword().equals(user.getPassword())) {
      if (!isPasswordValid(user.getPassword())) {
        user.setPassword(userDatabase.getPassword());
        throw new NotValidPasswordException(PASSWORD_EXCEPTION_MESSAGE);
      }
      String encryptedPassword = PasswordEncryption.encryptPassword(user.getPassword());
      user.setPassword(encryptedPassword);
    }
    LOGGER.info("User with id {" + user.getId() + "} was updated in the database");
    userRepository.update(user);
  }

  /**
   * Retrieves a user with the specified username. If no user is found, a NotFoundException is
   * thrown.
   *
   * @param username The username of the user to retrieve
   * @return The user with the specified username
   */
  public User getUserByUsername(String username) {
    return userRepository.getUserByUsername(username)
        .orElseThrow(() -> new NotFoundException(
            "User with username {" + username + "} is not in the database"));
  }

  /**
   * Checks if the specified password is valid. A password is valid if it matches the password
   * regex.
   *
   * @param password The password to check
   * @return True if the password is valid, false otherwise
   */
  public boolean isPasswordValid(String password) {
    return password.matches(PASSWORD_REGEX);
  }

  /**
   * Retrieves all users with the specified active status.
   *
   * @param active The active status of the users to retrieve from the database (true or false)
   * @return A list of all users with the specified active status
   */
  public List<User> getUsersByActive(boolean active) {
    return userRepository.getAllUsersByActive(active);
  }

  /**
   * Closes the database connection.
   */
  public void close() {
    try {
      userRepository.closeStatements();
    } catch (Exception e) {
      LOGGER.error("Error closing the database connection");
    }

  }
}
