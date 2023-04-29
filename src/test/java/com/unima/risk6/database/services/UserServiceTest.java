package com.unima.risk6.database.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.unima.risk6.database.configurations.DatabaseConfiguration;
import com.unima.risk6.database.exceptions.NotFoundException;
import com.unima.risk6.database.models.GameStatistic;
import com.unima.risk6.database.models.User;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for user service
 *
 * @author astoyano
 */

class UserServiceTest {

  private static UserService userService;

  private static GameStatisticService gameStatisticService;

  @BeforeEach
  void setUp() {
    try {
      DatabaseConfiguration.startDatabaseConfiguration();
      Connection connection = DatabaseConfiguration.getDatabaseConnection();
      connection.setAutoCommit(false);
      userService = DatabaseConfiguration.getUserService();
      gameStatisticService = DatabaseConfiguration.getGameStatisticService();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }

  }

  @AfterEach
  void afterEach() throws SQLException {
    DatabaseConfiguration.getDatabaseConnection().close();
  }

  @Test
  void saveUser() {
    User newUser = new User("astoyano", "12345Test!", "/com/unima/risk6/images/test.png");
    userService.saveUser(newUser);
    Long userId = newUser.getId();
    User databaseUser = userService.getUserById(userId);
    assertEquals(newUser, databaseUser);


  }

  @Test
  void deleteUserById() {
    User newUser = new User("astoyano", "12345Test!", "/com/unima/risk6/images/test.png");
    userService.saveUser(newUser);
    Long userId = newUser.getId();
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
  void updateUser() {
    User newUser = new User("astoyano", "12345Test!", "/com/unima/risk6/images/test.png");
    userService.saveUser(newUser);
    Long userId = newUser.getId();
    User user = userService.getUserById(userId);
    user.setUsername("AndreyStoyanov");
    userService.updateUser(user);
    User databaseUser = userService.getUserById(userId);
    assertEquals(databaseUser, user);
  }

  @Test
  void getAllStatisticsByUserId() {
    User newUser = new User("astoyano", "12345Test!", "/com/unima/risk6/images/test.png");
    userService.saveUser(newUser);
    GameStatistic gameStatistic = new GameStatistic(newUser);
    GameStatistic gameStatistic1 = new GameStatistic(newUser);
    gameStatisticService.saveGameStatistic(gameStatistic);
    gameStatisticService.saveGameStatistic(gameStatistic1);
    gameStatisticService.updateGameStatisticAfterGame(gameStatistic);
    gameStatisticService.updateGameStatisticAfterGame(gameStatistic1);
    assertEquals(2, gameStatisticService.getAllStatisticsByUserId(newUser.getId()).size());
    assertEquals(List.of(gameStatistic.getId(), gameStatistic1.getId()),
        List.of(gameStatisticService.getAllStatisticsByUserId(newUser.getId()).get(0).getId(),
            gameStatisticService.getAllStatisticsByUserId(newUser.getId()).get(1).getId()));
  }
}