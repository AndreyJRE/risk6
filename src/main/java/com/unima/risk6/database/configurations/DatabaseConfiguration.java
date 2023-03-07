package com.unima.risk6.database.configurations;

import com.unima.risk6.database.AppDatabase;
import com.unima.risk6.database.models.GameStatistic;
import com.unima.risk6.database.repositories.GameStatisticRepository;
import com.unima.risk6.database.repositories.UserRepository;
import com.unima.risk6.database.services.GameStatisticService;
import com.unima.risk6.database.services.UserService;
import java.sql.Connection;

/**
 * Configuration of database for application
 *
 * @author astoyano
 */
public class DatabaseConfiguration {

  private static UserService userService;
  private static GameStatisticService gameStatisticService;
  private static UserRepository userRepository;
  private static GameStatisticRepository gameStatisticRepository;
  private static Connection databaseConnection;
  private static AppDatabase appDatabase;

  public static void startDatabaseConfiguration() {
    configureDatabaseConnection();
    configureRepositories();
    configureServices();
  }

  private static void configureServices() {
    userService = new UserService(userRepository);
    gameStatisticService = new GameStatisticService(gameStatisticRepository);
  }

  private static void configureRepositories() {
    userRepository = new UserRepository(databaseConnection);
    gameStatisticRepository = new GameStatisticRepository(databaseConnection, userRepository);
  }

  private static void configureDatabaseConnection() {
    String databasePath = "src/main/resources/com/unima/risk6/database/risk_database.db";
    appDatabase = new AppDatabase(databasePath);
    databaseConnection = appDatabase.getConnection();

  }

  public static UserService getUserService() {
    return userService;
  }

  public static UserRepository getUserRepository() {
    return userRepository;
  }

  public static Connection getDatabaseConnection() {
    return databaseConnection;
  }

  public static AppDatabase getAppDatabase() {
    return appDatabase;
  }

  public static GameStatisticService getGameStatisticService() {
    return gameStatisticService;
  }

  public static GameStatisticRepository getGameStatisticRepository() {
    return gameStatisticRepository;
  }
}
