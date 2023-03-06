package com.unima.risk6.database.repositories;

import com.unima.risk6.database.dao.GameStatisticDao;
import com.unima.risk6.database.models.GameStatistic;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of Game Statistic Dao
 *
 * @author
 */
public class GameStatisticRepository implements GameStatisticDao {

  private final Connection databaseConnection;

  private PreparedStatement addGameStatisticStatement;

  public GameStatisticRepository(Connection databaseConnection) {
    this.databaseConnection = databaseConnection;
    initStatements();
  }

  public void initStatements() {
    //TODO Make prepared statements for user
    try {
      addGameStatisticStatement = this.databaseConnection.prepareStatement("""
                    
          """);
    } catch (SQLException e) {
      //TODO Error handling

    }
  }


  @Override
  public Optional<GameStatistic> get(long id) {
    return Optional.empty();
  }

  @Override
  public List<GameStatistic> getAll() {
    return null;
  }

  @Override
  public void save(GameStatistic gameStatistic) {

  }

  @Override
  public void update(GameStatistic gameStatistic, String[] params) {

  }

  @Override
  public void delete(GameStatistic gameStatistic) {

  }


  @Override
  public List<GameStatistic> getAllStatisticsByUserId(Long id) {
    return null;
  }
}
