package com.unima.risk6.database.repositories;

import com.unima.risk6.database.daos.UserDao;
import com.unima.risk6.database.exceptions.NotFoundException;
import com.unima.risk6.database.models.GameStatistic;
import com.unima.risk6.database.models.User;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.sql.Connection;

/**
 * Implementation of user dao
 *
 * @author astoyano
 */

public class UserRepository implements UserDao {

  private final Connection databaseConnection;
  private PreparedStatement addUserStatement;

  private PreparedStatement deleteUserStatement;

  private PreparedStatement updateUserStatement;

  private PreparedStatement getUserStatement;

  private PreparedStatement getUsersStatement;

  private PreparedStatement getAllStatisticsByUserIdStatement;

  private final DateTimeFormatter dtf;

  public UserRepository(Connection databaseConnection) {
    this.databaseConnection = databaseConnection;
    dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    initStatements();
  }

  private void initStatements() {
    try {
      addUserStatement = this.databaseConnection.prepareStatement("""
          INSERT INTO user (username, password, active, created_at, image_path)
          VALUES (?,?,?,?,?)""", Statement.RETURN_GENERATED_KEYS);
      deleteUserStatement = this.databaseConnection.prepareStatement("""
          DELETE FROM user WHERE id=?""");
      updateUserStatement = this.databaseConnection.prepareStatement("""
          UPDATE user SET active = ?,image_path=?,username=?,password=? WHERE id=?""");
      getUserStatement = this.databaseConnection.prepareStatement("""
          SELECT * FROM user WHERE id=?""");
      getUsersStatement = this.databaseConnection.prepareStatement("""
          SELECT * FROM user""");
      getAllStatisticsByUserIdStatement = this.databaseConnection.prepareStatement("""
          SELECT * FROM game_statistic WHERE user_id=?""");
    } catch (SQLException e) {
      throw new RuntimeException(e);

    }
  }

  @Override
  public Optional<User> get(Long id) {
    try {
      getUserStatement.setLong(1, id);
      ResultSet rs = getUserStatement.executeQuery();
      Optional<User> user = Optional.empty();
      if (rs.next()) {
        String username = rs.getString(2);
        String password = rs.getString(3);
        boolean active = rs.getInt(4) == 1;
        LocalDate createdAt = LocalDate.parse(rs.getString(5));
        String imagePath = rs.getString(6);
        user = Optional.of(new User(id, username, password, imagePath, active,
            createdAt));


      }
      rs.close();
      return user;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<User> getAll() {
    try {
      ResultSet rs = getUsersStatement.executeQuery();
      List<User> users = new ArrayList<>();
      while (rs.next()) {
        Long userId = rs.getLong(1);
        String username = rs.getString(2);
        String password = rs.getString(3);
        boolean active = rs.getInt(4) == 1;
        LocalDate createdAt = LocalDate.parse(rs.getString(5));
        String imagePath = rs.getString(6);
        User user = new User(userId, username, password, imagePath, active, createdAt);
        users.add(user);

      }
      rs.close();
      return users;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void save(User user) {
    try {
      addUserStatement.setString(1, user.getUsername());
      addUserStatement.setString(2, user.getPassword());
      addUserStatement.setInt(3, 1);
      addUserStatement.setString(4, user.getCreatedAt().format(dtf));
      addUserStatement.setString(5, user.getImagePath());
      addUserStatement.execute();
      ResultSet generatedKeys = addUserStatement.getGeneratedKeys();
      if (generatedKeys.next()) {
        Long id = generatedKeys.getLong(1);
        user.setId(id);
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void update(User user) {
    try {
      updateUserStatement.setInt(1, user.isActive() ? 1 : 0);
      updateUserStatement.setString(2, user.getImagePath());
      updateUserStatement.setString(3, user.getUsername());
      updateUserStatement.setString(4, user.getPassword());
      updateUserStatement.setLong(5, user.getId());
      updateUserStatement.execute();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void deleteById(Long id) {
    try {
      deleteUserStatement.setLong(1, id);
      deleteUserStatement.execute();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<GameStatistic> getAllStatisticsByUserId(Long id) {
    try {
      getAllStatisticsByUserIdStatement.setLong(1, id);
      ResultSet rs = getAllStatisticsByUserIdStatement.executeQuery();
      List<GameStatistic> statistics = new ArrayList<>();
      while (rs.next()) {
        Long statisticId = rs.getLong(1);
        User user = get(id).orElseThrow(() -> new NotFoundException(
            "User with id {" + id + "} is not in "
            + "the "
            + "database"));
        int troopsLost = rs.getInt(3);
        int troopsGained = rs.getInt(4);
        boolean gameWon = rs.getInt(5) == 1;
        LocalDateTime startDate = LocalDateTime.parse(rs.getString(6));
        LocalDateTime finishDate = LocalDateTime.parse(rs.getString(7));

        GameStatistic gameStatistic = new GameStatistic(statisticId, user, startDate, finishDate,
            troopsLost, troopsGained, gameWon);
        statistics.add(gameStatistic);
      }
      rs.close();
      return statistics;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }


  }
}
