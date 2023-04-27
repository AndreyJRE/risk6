package com.unima.risk6.database.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

class GameStatisticServiceTest {

  private static GameStatisticService gameStatisticService;
  private static UserService userService;

  @BeforeEach
  void setUp() {
    try {
      DatabaseConfiguration.startDatabaseConfiguration();
      Connection connection = DatabaseConfiguration.getDatabaseConnection();
      connection.setAutoCommit(false);
      gameStatisticService = DatabaseConfiguration.getGameStatisticService();
      userService = DatabaseConfiguration.getUserService();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }

  }

  @AfterEach
  void afterEach() throws SQLException {
    DatabaseConfiguration.getDatabaseConnection().close();
  }

  @Test
  void saveGameStatistic() {
    User newUser = new User("testUser", "12345Andrey!"
        , "/com/unima/risk6/images/test.png");
    userService.saveUser(newUser);
    User databaseUser = userService.getUserById(newUser.getId());

    GameStatistic gameStatistic = new GameStatistic(databaseUser);
    gameStatisticService.saveGameStatistic(gameStatistic);
    gameStatisticService.updateGameStatisticAfterGame(gameStatistic);
    List<GameStatistic> userStatistics = gameStatisticService.getAllStatisticsByUserId(
        newUser.getId());

    assertEquals(1, userStatistics.size());
    assertEquals(gameStatistic.getId(), userStatistics.get(0).getId());
  }

  @Test
  void updateGameStatistic() {
    User newUser = new User("testUser", "12345Andrey!"
        , "/com/unima/risk6/images/test.png");
    userService.saveUser(newUser);
    User databaseUser = userService.getUserById(newUser.getId());

    GameStatistic gameStatistic = new GameStatistic(databaseUser);
    gameStatisticService.saveGameStatistic(gameStatistic);

    GameStatistic gameStatisticToUpdate = gameStatisticService.getGameStatisticById(
        gameStatistic.getId());
    gameStatisticToUpdate.setTroopsLost(15);
    gameStatisticService.updateGameStatisticAfterGame(gameStatisticToUpdate);

    GameStatistic updatedGameStatistic = gameStatisticService.getGameStatisticById(
        gameStatisticToUpdate.getId());
    assertEquals(15, updatedGameStatistic.getTroopsLost());
  }

  @Test
  void getAllStatisticsByUserId_NotFound() {
    assertThrows(NotFoundException.class,
        () -> gameStatisticService.getAllStatisticsByUserId(Long.MAX_VALUE));
  }

  @Test
  void getGameStatistic() {
    User newUser = new User("testUser", "12345Andrey!"
        , "/com/unima/risk6/images/test.png");
    userService.saveUser(newUser);
    User databaseUser = userService.getUserById(newUser.getId());
    assertEquals(newUser, databaseUser);
    GameStatistic gameStatistic = new GameStatistic(databaseUser);
    gameStatisticService.saveGameStatistic(gameStatistic);
    GameStatistic gameStatistic1 = gameStatisticService.getGameStatisticById(gameStatistic.getId());
    assertEquals(gameStatistic.getId(), gameStatistic1.getId());

  }

}
