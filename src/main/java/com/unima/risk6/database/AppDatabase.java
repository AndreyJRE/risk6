package com.unima.risk6.database;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database creating and connection configuration
 *
 * @author astoyano
 */
public class AppDatabase {

  private final String databasePath;
  private Connection conn;

  /**
   * Constructor for the AppDatabase class that takes the database path as a parameter and
   * initializes the database connection.
   *
   * @param databasePath the path to the database
   */
  public AppDatabase(final String databasePath) {
    this.databasePath = databasePath;
    init();
  }

  /**
   * The init method initializes the database connection and creates the database if it does not yet
   * exist.
   */
  private void init() {

    conn = null;
    try {
      Class.forName("org.sqlite.JDBC");
      Path pathToDatabaseDirectory = Path.of("dbFiles");
      if (!Files.exists(pathToDatabaseDirectory)) {
        Files.createDirectory(pathToDatabaseDirectory);
      }
      conn = DriverManager.getConnection("jdbc:sqlite:" + databasePath);
      createDatabase();
    } catch (SQLException | ClassNotFoundException | IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * The createDatabase method creates the database if it does not yet exist.
   *
   * @throws SQLException if the database cannot be created
   */
  private void createDatabase() throws SQLException {
    creatUserTable();
    creatGameStatisticTable();
  }

  /**
   * The creatUserTable method creates the user table if it does not yet exist.
   *
   * @throws SQLException if the user table cannot be created
   */
  private void creatUserTable() throws SQLException {
    Statement statement = conn.createStatement();
    String createSql = """
        CREATE TABLE IF NOT EXISTS user (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT
        , username TEXT NOT NULL UNIQUE, password TEXT, active INTEGER DEFAULT 1
        ,created_at TEXT, image_path TEXT)""";
    statement.execute(createSql);
  }

  /**
   * The creatGameStatisticTable method creates the game_statistic table if it does not yet exist.
   *
   * @throws SQLException if the game_statistic table cannot be created
   */
  private void creatGameStatisticTable() throws SQLException {
    Statement statement = conn.createStatement();
    String createSql = """
        CREATE TABLE IF NOT EXISTS game_statistic (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT
        ,user_id INTEGER NOT NULL,troops_lost INTEGER,troops_gained INTEGER
        ,game_won INTEGER,start_date TEXT,finish_date TEXT,countries_won INTEGER
        ,countries_lost INTEGER,FOREIGN KEY (user_id) REFERENCES user (id))""";
    statement.execute(createSql);
  }

  public Connection getConnection() {
    return conn;
  }
}
