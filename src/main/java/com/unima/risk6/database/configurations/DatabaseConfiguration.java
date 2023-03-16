package com.unima.risk6.database.configurations;

import com.unima.risk6.database.AppDatabase;
import com.unima.risk6.database.repositories.GameStatisticRepository;
import com.unima.risk6.database.repositories.UserRepository;
import com.unima.risk6.database.services.GameStatisticService;
import com.unima.risk6.database.services.UserService;
import java.sql.Connection;

/**
 * This class represents the configuration of the database for the application. It provides various
 * methods to configure the database connection, repositories, and services. The purpose of this
 * class is to ensure that the application has access to the necessary database resources and to
 * facilitate the interaction between the application and the database.
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

  /**
   * The method is called to initialize the database configuration by calling the
   * configureDatabaseConnection, configureRepositories, and configureServices methods.
   */
  public static void startDatabaseConfiguration() {
    configureDatabaseConnection();
    configureRepositories();
    configureServices();
  }

  /**
   * The configureServices method initializes the UserService and GameStatisticService using the
   * UserRepository and GameStatisticRepository respectively.
   */
  private static void configureServices() {
    userService = new UserService(userRepository);
    gameStatisticService = new GameStatisticService(gameStatisticRepository);
  }

  /**
   * The configureRepositories method initializes the UserRepository and GameStatisticRepository
   * using the Connection.
   */
  private static void configureRepositories() {
    userRepository = new UserRepository(databaseConnection);
    gameStatisticRepository = new GameStatisticRepository(databaseConnection, userRepository);
  }

  /**
   * The configureDatabaseConnection method creates an instance of the AppDatabase using the
   * database path and establishes a connection to the database.
   */
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
