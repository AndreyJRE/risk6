package com.unima.risk6.database.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.unima.risk6.database.configurations.DatabaseConfiguration;
import com.unima.risk6.database.exceptions.NotFoundException;
import com.unima.risk6.database.models.User;
import java.sql.Connection;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

/**
 * Tests for user service
 *
 * @author astoyano
 */

class UserServiceTest {

  private static UserService userService;

  private static Connection connection;

  private Long userId;

  @BeforeAll
  static void setUp() {
    try {
      DatabaseConfiguration.startDatabaseConfiguration();
      connection = DatabaseConfiguration.getDatabaseConnection();
      connection.setAutoCommit(false);
      userService = DatabaseConfiguration.getUserService();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }

  }


  @Test
  @Order(1)
  void saveUser() {
    User newUser = new User("astoyano", "password"
        , "/com/unima/risk6/images/test.png");
    userService.saveUser(newUser);
    userId = newUser.getId();
    User databaseUser = userService.getUserById(userId);
    assertEquals(newUser, databaseUser);


  }

  @Test
  @Order(3)
  void deleteUserById() {

    userService.deleteUserById(userId);
    try {
      userService.getUserById(userId);
      fail("Should have thrown an not found exception");
    } catch (NotFoundException e) {
      final String expected = "User with id {" + userId + "} is not in the database";
      assertEquals(expected, e.getMessage());
    }

  }

  @Test
  @Order(2)
  void updateUser() {
    User user = userService.getUserById(userId);
    user.setUsername("AndreyStoyanov");
    userService.updateUser(user);
    User databaseUser = userService.getUserById(userId);
    assertEquals(databaseUser, user);
  }

  @Test
  void getAllStatisticsByUserId() {
  }
}