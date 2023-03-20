package com.unima.risk6.database.repositories;

import com.unima.risk6.database.daos.GameStatisticDao;
import com.unima.risk6.database.exceptions.NotFoundException;
import com.unima.risk6.database.models.GameStatistic;
import com.unima.risk6.database.models.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository class for accessing and managing GameStatistic objects in the database. Implements the
 * interface.
 *
 * @author astoyano
 */
public class GameStatisticRepository implements GameStatisticDao {

  private final Connection databaseConnection;

  private PreparedStatement addGameStatisticStatement;

  private PreparedStatement getGameStatisticByIdStatement;

  private PreparedStatement updateGameStatisticStatement;

  private PreparedStatement getAllGameStatisticsStatement;

  private final UserRepository userRepository;

  private final DateTimeFormatter dtf;

  /**
   * Constructs a new instance of the GameStatisticRepository.
   *
   * @param databaseConnection the connection to the database
   * @param userRepository     the UserRepository used to retrieve user data from the database
   */


  public GameStatisticRepository(Connection databaseConnection, UserRepository userRepository) {
    this.databaseConnection = databaseConnection;
    this.userRepository = userRepository;
    dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    initStatements();
  }

  /**
   * Initializes the prepared statements used by this repository.
   */
  private void initStatements() {
    try {
      addGameStatisticStatement = this.databaseConnection.prepareStatement("""
              INSERT INTO game_statistic (user_id,start_date) VALUES (?,?)""",
          Statement.RETURN_GENERATED_KEYS);
      getGameStatisticByIdStatement = this.databaseConnection.prepareStatement("""
          SELECT * FROM game_statistic WHERE id=?""");
      getAllGameStatisticsStatement = this.databaseConnection.prepareStatement("""
          SELECT * FROM game_statistic""");
      updateGameStatisticStatement = this.databaseConnection.prepareStatement("""
          UPDATE game_statistic SET finish_date=?,game_won=?,troops_gained=?,troops_lost=? WHERE id=?""");
    } catch (SQLException e) {
      throw new RuntimeException(e);

    }
  }

  /**
   * Retrieves a GameStatistic object from the database by its ID.
   *
   * @param id the ID of the game statistic to retrieve
   * @return an Optional containing the retrieved GameStatistic object, or empty if it was not found
   * @throws NotFoundException if the user associated with the game statistic is not found in the
   *                           database
   */
  @Override
  public Optional<GameStatistic> get(Long id) {
    try {
      getGameStatisticByIdStatement.setLong(1, id);
      ResultSet rs = getGameStatisticByIdStatement.executeQuery();
      Optional<GameStatistic> gameStatistic = Optional.empty();
      if (rs.next()) {
        Long userId = rs.getLong(2);
        User user = userRepository.get(userId).orElseThrow(() ->
            new NotFoundException("User with id {" + userId + "} is not in the database"));
        int troopsLost = rs.getInt(3);
        int troopsGained = rs.getInt(4);
        boolean gameWon = rs.getInt(5) == 1;
        LocalDateTime startDate = LocalDateTime.parse(rs.getString(6));
        LocalDateTime finishDate = LocalDateTime.parse(rs.getString(7));
        gameStatistic = Optional.of(new GameStatistic(id, user, startDate, finishDate,
            troopsLost, troopsGained, gameWon));

      }
      rs.close();
      return gameStatistic;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Retrieves a list of all GameStatistic objects in the database.
   *
   * @return a list of GameStatistic objects
   * @throws NotFoundException if the user associated with a game statistic is not found in the
   *                           database
   */


  @Override
  public List<GameStatistic> getAll() {
    try {
      ResultSet rs = getAllGameStatisticsStatement.executeQuery();
      List<GameStatistic> statistics = new ArrayList<>();
      while (rs.next()) {
        Long gameId = rs.getLong(1);
        Long userId = rs.getLong(2);
        User user = userRepository.get(userId).orElseThrow(() ->
            new NotFoundException("User with id {" + userId + "} is not in the database"));
        int troopsLost = rs.getInt(3);
        int troopsGained = rs.getInt(4);
        boolean gameWon = rs.getInt(5) == 1;
        LocalDateTime startDate = LocalDateTime.parse(rs.getString(6), dtf);
        LocalDateTime finishDate = LocalDateTime.parse(rs.getString(7), dtf);
        GameStatistic gameStatistic = new GameStatistic(gameId, user, startDate, finishDate,
            troopsLost, troopsGained, gameWon);
        statistics.add(gameStatistic);
      }
      rs.close();
      return statistics;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Saves a  GameStatistic object to the database.
   *
   * @param gameStatistic the game statistic to save
   * @return the ID of the saved game statistic
   */

  @Override
  public Long save(GameStatistic gameStatistic) {
    try {
      addGameStatisticStatement.setLong(1, gameStatistic.getUser().getId());
      addGameStatisticStatement.setString(2, LocalDateTime.now().format(dtf));
      addGameStatisticStatement.execute();
      ResultSet generatedKeys = addGameStatisticStatement.getGeneratedKeys();
      Long id = null;
      if (generatedKeys.next()) {
        id = generatedKeys.getLong(1);
        gameStatistic.setId(id);
      }
      return id;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Updates a GameStatistic object in the database.
   *
   * @param gameStatistic the game statistic to update
   */
  @Override
  public void update(GameStatistic gameStatistic) {
    try {
      updateGameStatisticStatement.setString(1
          , gameStatistic.getFinishDate().format(dtf));
      updateGameStatisticStatement.setInt(2, gameStatistic.isGameWon() ? 1 : 0);
      updateGameStatisticStatement.setInt(3, gameStatistic.getTroopsGained());
      updateGameStatisticStatement.setInt(4, gameStatistic.getTroopsLost());
      updateGameStatisticStatement.setLong(5, gameStatistic.getId());
      updateGameStatisticStatement.execute();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * This method should stay empty, because it is not allowed to delete own statistic. But it must
   * be overriden.
   *
   * @param id The id of game statistic to delete
   */
  @Override
  public void deleteById(Long id) {

  }


}
