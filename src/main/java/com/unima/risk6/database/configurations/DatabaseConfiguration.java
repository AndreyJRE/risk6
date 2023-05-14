package com.unima.risk6.database.configurations;

import com.unima.risk6.database.AppDatabase;
import com.unima.risk6.database.repositories.GameStatisticRepository;
import com.unima.risk6.database.repositories.UserRepository;
import com.unima.risk6.database.services.GameStatisticService;
import com.unima.risk6.database.services.UserService;
import java.sql.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
  private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConfiguration.class);

  /**
   * The method is called to initialize the database configuration by calling the
   * configureDatabaseConnection, configureRepositories, and configureServices methods.
   */
  public static void startDatabaseConfiguration() {
    configureDatabaseConnection();
    LOGGER.info("Database connection was established");
    configureRepositories();
    configureServices();
    LOGGER.info("Database services were configured and database is ready to use");
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
    String databasePath = "dbFiles/risk_database.db";
    AppDatabase appDatabase = new AppDatabase(databasePath);
    databaseConnection = appDatabase.getConnection();

  }

  /**
   * Closes the database connection and services.
   */
  public static void closeDatabaseConnectionAndServices() {
    try {
      userService.close();
      gameStatisticService.close();
      databaseConnection.close();
    } catch (Exception e) {
      LOGGER.error("Error closing database connection: " + e.getMessage());
    }
  }

  /**
   * Returns the UserService.
   *
   * @return The UserService.
   */
  public static UserService getUserService() {
    return userService;
  }

  /**
   * Returns the database connection.
   *
   * @return The database connection.
   */
  public static Connection getDatabaseConnection() {
    return databaseConnection;
  }

  /**
   * Returns the GameStatisticService.
   *
   * @return The GameStatisticService.
   */
  public static GameStatisticService getGameStatisticService() {
    return gameStatisticService;
  }
}
