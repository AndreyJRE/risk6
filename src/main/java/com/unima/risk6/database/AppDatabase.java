package com.unima.risk6.database;


import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database creating and configuration
 *
 * @author astoyano
 */
public class AppDatabase {

  private final String databasePath;
  private Connection conn;

  public AppDatabase(final String databasePath) {
    this.databasePath = databasePath;
  }

  public void init() {
    File databaseFile = new File(databasePath);
    conn = null;
    try {
      if (!databaseFile.exists()) {
        databaseFile.createNewFile();
      }
      conn = DriverManager.getConnection("jdbc:sqlite:" + databaseFile.getPath());
      createDatabase();
    } catch (SQLException e) {
      //TODO Error handling
    } catch (IOException e) {
      //TODO Error handling
    }
  }

  public void createDatabase() throws SQLException {
    creatUserTable();
  }

  public void creatUserTable() throws SQLException {
    Statement statement = conn.createStatement();
    //TODO
    String createSql = """
        CREATE TABLE IF NOT EXISTS user
        """;
    statement.execute(createSql);
  }

  public Connection getConnection() {
    return conn;
  }
}
