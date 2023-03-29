package com.unima.risk6.database;


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

  public AppDatabase(final String databasePath) {
    this.databasePath = databasePath;
    init();
  }

  private void init() {
    conn = null;
    try {
      Class.forName("org.sqlite.JDBC");
      conn = DriverManager.getConnection("jdbc:sqlite:" + databasePath);
      createDatabase();
    } catch (SQLException | ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  private void createDatabase() throws SQLException {
    creatUserTable();
    creatGameStatisticTable();
  }

  private void creatUserTable() throws SQLException {
    Statement statement = conn.createStatement();
    String createSql = """
        CREATE TABLE IF NOT EXISTS user (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT
        , username TEXT NOT NULL UNIQUE, password TEXT, active INTEGER DEFAULT 1
        ,created_at TEXT, image_path TEXT)
        """;
    statement.execute(createSql);
  }

  private void creatGameStatisticTable() throws SQLException {
    Statement statement = conn.createStatement();
    String createSql = """
        CREATE TABLE IF NOT EXISTS game_statistic (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT
        ,user_id INTEGER NOT NULL,troops_lost INTEGER
        ,troops_gained INTEGER,game_won INTEGER,start_date TEXT
        ,finish_date TEXT,FOREIGN KEY (user_id) REFERENCES user (id))
        """;
    statement.execute(createSql);
  }


  public Connection getConnection() {
    return conn;
  }
}
