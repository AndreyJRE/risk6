package com.unima.risk6.database;


import java.io.File;
import java.io.IOException;
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

  public void init() {
    File databaseFile = new File(databasePath);
    conn = null;
    try {
      if (!databaseFile.exists()) {
        databaseFile.createNewFile();
      }
      Class.forName("org.sqlite.JDBC");
      conn = DriverManager.getConnection("jdbc:sqlite:" + databaseFile.getPath());
      createDatabase();
    } catch (SQLException | IOException | ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  public void createDatabase() throws SQLException {
    creatUserTable();
    creatGameStatisticTable();
  }

  public void creatUserTable() throws SQLException {
    Statement statement = conn.createStatement();
    String createSql = """
        CREATE TABLE IF NOT EXISTS user (id INTEGER PRIMARY KEY, username TEXT NOT NULL
        ,password TEXT, active INTEGER, created_at TEXT, image_path TEXT)
        """;
    statement.execute(createSql);
  }

  public void creatGameStatisticTable() throws SQLException {
    Statement statement = conn.createStatement();
    String createSql = """
        CREATE TABLE IF NOT EXISTS game_statistic (id INTEGER PRIMARY KEY,user_id INTEGER NOT NULL
        ,troops_lost INTEGER, troops_gained INTEGER,game_won INTEGER,start_date TEXT
        ,finish_date TEXT,FOREIGN KEY (user_id) REFERENCES user (id))
        """;
    statement.execute(createSql);
  }


  public Connection getConnection() {
    return conn;
  }
}
