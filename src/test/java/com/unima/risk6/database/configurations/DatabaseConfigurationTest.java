package com.unima.risk6.database.configurations;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DatabaseConfigurationTest {

  @BeforeEach
  void setUp() {
    DatabaseConfiguration.startDatabaseConfiguration();
  }

  @Test
  void startDatabaseConfiguration() {
    assertNotNull(DatabaseConfiguration.getDatabaseConnection());
    assertNotNull(DatabaseConfiguration.getUserService());
    assertNotNull(DatabaseConfiguration.getGameStatisticService());
  }

  @Test
  void closeDatabaseConnection() throws SQLException {
    DatabaseConfiguration.closeDatabaseConnectionAndServices();
    assertTrue(DatabaseConfiguration.getDatabaseConnection().isClosed());
  }
}