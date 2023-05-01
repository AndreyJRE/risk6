package com.unima.risk6.database.repositories;

import com.unima.risk6.database.daos.UserDao;
import com.unima.risk6.database.models.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A repository for performing CRUD operations on User objects in a database. Implements the UserDao
 * interface. This class provides methods for adding, updating, deleting, and retrieving users from
 * a database, as well as retrieving game statistics associated with a user. It uses prepared
 * statements to execute SQL queries on a database connection provided during instantiation.
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

  private PreparedStatement getUserByUsernameStatement;

  private PreparedStatement deleteGameStatisticsByUserIdStatement;

  private final DateTimeFormatter localDateDtf;

  /**
   * Constructs a new UserRepository with the provided database connection.
   *
   * @param databaseConnection a Connection object representing the database connection
   */
  public UserRepository(Connection databaseConnection) {
    this.databaseConnection = databaseConnection;
    localDateDtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    initStatements();
  }

  /**
   * Initializes prepared statements for this repository. Creates prepared statements for adding,
   * deleting, updating, and retrieving users from the database, as well as retrieving game
   * statistics associated with a user.
   *
   * @throws RuntimeException if there is a problem initializing the statements
   */
  private void initStatements() {
    try {
      addUserStatement = this.databaseConnection.prepareStatement("""
          INSERT INTO user (username, password, created_at, image_path)
          VALUES (?,?,?,?)""", Statement.RETURN_GENERATED_KEYS);
      deleteUserStatement = this.databaseConnection.prepareStatement("""
          DELETE FROM user WHERE id=?""");
      updateUserStatement = this.databaseConnection.prepareStatement("""
          UPDATE user SET active = ?,image_path=?,username=?,password=? WHERE id=?""");
      getUserStatement = this.databaseConnection.prepareStatement("""
          SELECT * FROM user WHERE id=?""");
      getUsersStatement = this.databaseConnection.prepareStatement("""
          SELECT * FROM user""");
      getUserByUsernameStatement = this.databaseConnection.prepareStatement("""
          SELECT id,password,image_path,active,created_at FROM user WHERE username = ?""");
      deleteGameStatisticsByUserIdStatement = this.databaseConnection.prepareStatement("""
          DELETE FROM game_statistic WHERE user_id=?""");
    } catch (SQLException e) {
      throw new RuntimeException(e);

    }
  }

  /**
   * Retrieves a User object with the specified ID from the database.
   *
   * @param id a Long representing the ID of the user to retrieve
   * @return an Optional containing the User object if found, or an empty Optional otherwise
   * @throws RuntimeException if there is a problem executing the query
   */
  @Override
  public Optional<User> get(Long id) {
    try {
      getUserStatement.setLong(1, id);
      ResultSet rs = getUserStatement.executeQuery();
      Optional<User> user = Optional.empty();
      if (rs.next()) {
        String username = rs.getString(2);
        String password = rs.getString(3);
        boolean active = rs.getBoolean(4);
        LocalDate createdAt = LocalDate.parse(rs.getString(5), localDateDtf);
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

  /**
   * Retrieves a list of all User objects in the database.
   *
   * @return a List of User objects
   * @throws RuntimeException if there is a problem executing the query
   */
  @Override
  public List<User> getAll() {
    try {
      ResultSet rs = getUsersStatement.executeQuery();
      List<User> users = new ArrayList<>();
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
      while (rs.next()) {
        Long userId = rs.getLong(1);
        String username = rs.getString(2);
        String password = rs.getString(3);
        boolean active = rs.getBoolean(4);
        LocalDate createdAt = LocalDate.parse(rs.getString(5), formatter);
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

  /**
   * Adds a User object to the database.
   *
   * @param user a User object to add to the database
   * @return the ID of the newly added User object, or null if the add operation failed
   * @throws RuntimeException if there is a problem executing the query
   */
  @Override
  public Long save(User user) {
    try {
      addUserStatement.setString(1, user.getUsername());
      addUserStatement.setString(2, user.getPassword());
      addUserStatement.setString(3, user.getCreatedAt().format(localDateDtf));
      addUserStatement.setString(4, user.getImagePath());
      addUserStatement.execute();
      ResultSet generatedKeys = addUserStatement.getGeneratedKeys();
      Long id = null;
      if (generatedKeys.next()) {
        id = generatedKeys.getLong(1);
        user.setId(id);
      }
      return id;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Updates a User object in the database.
   *
   * @param user a User object to update in the database
   * @throws RuntimeException if there is a problem executing the query
   */
  @Override
  public void update(User user) {
    try {
      updateUserStatement.setBoolean(1, user.isActive());
      updateUserStatement.setString(2, user.getImagePath());
      updateUserStatement.setString(3, user.getUsername());
      updateUserStatement.setString(4, user.getPassword());
      updateUserStatement.setLong(5, user.getId());
      updateUserStatement.execute();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Deletes a User object with the specified ID from the database.
   *
   * @param id a Long representing the ID of the user to delete
   * @throws RuntimeException if there is a problem executing the query
   */
  @Override
  public void deleteById(Long id) {
    try {
      deleteUserStatement.setLong(1, id);
      deleteGameStatisticsByUserIdStatement.setLong(1, id);
      deleteUserStatement.execute();
      deleteGameStatisticsByUserIdStatement.execute();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Retrieves a User object with the specified username from the database. If the user is not
   * found, an empty Optional is returned.
   *
   * @param username a String representing the username of the user to retrieve
   * @return an Optional containing the User object if found, or an empty Optional otherwise
   * @throws RuntimeException if there is a problem executing the query
   */
  @Override
  public Optional<User> getUserByUsername(String username) {
    try {
      getUserByUsernameStatement.setString(1, username);
      ResultSet rs = getUserByUsernameStatement.executeQuery();
      Optional<User> user = Optional.empty();
      if (rs.next()) {
        Long id = rs.getLong(1);
        String password = rs.getString(2);
        String imagePath = rs.getString(3);
        boolean active = rs.getBoolean(4);
        LocalDate createdAt = LocalDate.parse(rs.getString(5), localDateDtf);
        user = Optional.of(new User(id, username, password, imagePath, active,
            createdAt));
      }
      rs.close();
      return user;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Retrieves a list of all User objects in the database that have the specified active status.
   *
   * @param active a boolean representing the active status of the users to retrieve
   * @return a List of User objects with the specified active status
   */
  @Override
  public List<User> getAllUsersByActive(boolean active) {
    return getAll().stream().filter(x -> x.isActive() == active).toList();
  }

  /**
   * Closes all PreparedStatements used by this DAO.
   *
   * @throws RuntimeException if there is a problem closing the PreparedStatements
   */
  public void closeStatements() {
    try {
      getUserStatement.close();
      getUsersStatement.close();
      addUserStatement.close();
      updateUserStatement.close();
      deleteUserStatement.close();
      getUserByUsernameStatement.close();
      deleteGameStatisticsByUserIdStatement.close();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
