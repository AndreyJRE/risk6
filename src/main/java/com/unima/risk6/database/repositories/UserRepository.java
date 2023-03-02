package com.unima.risk6.database.repositories;

import com.unima.risk6.database.dao.UserDao;
import com.unima.risk6.database.models.User;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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

  public UserRepository(Connection databaseConnection) {
    this.databaseConnection = databaseConnection;
    initStatements();
  }

  public void initStatements() {
    //TODO Make prepared statements for user
    try {
      addUserStatement = this.databaseConnection.prepareStatement("""
          
          """);
    } catch (SQLException e) {
      //TODO Error handling

    }
  }

  @Override
  public Optional<User> get(long id) {
    return Optional.empty();
  }

  @Override
  public List<User> getAll() {
    return null;
  }

  @Override
  public void save(User user) {

  }

  @Override
  public void update(User user, String[] params) {

  }

  @Override
  public void delete(User user) {

  }
}
