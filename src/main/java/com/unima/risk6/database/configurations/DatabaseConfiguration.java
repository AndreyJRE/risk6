package com.unima.risk6.database.configurations;

import com.unima.risk6.database.AppDatabase;
import com.unima.risk6.database.repositories.UserRepository;
import com.unima.risk6.database.services.UserService;
import java.sql.Connection;

/**
 * Configuration of database for application
 *
 * @author astoyano
 */
public class DatabaseConfiguration {

  private static UserService userService;
  private static UserRepository userRepository;
  private static Connection databaseConnection;
  private static AppDatabase appDatabase;

  public static void startDatabaseConfiguration() {
    configureDatabaseConnection();
    configureRepositories();
    configureServices();
  }

  public static void configureServices() {
    userService = new UserService(userRepository);
  }

  public static void configureRepositories() {
    userRepository = new UserRepository(databaseConnection);
  }

  public static void configureDatabaseConnection() {
    //TODO
    String databasePath = "";
    appDatabase = new AppDatabase(databasePath);
    appDatabase.init();
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
}
