package com.unima.risk6.gui.configurations;

import com.unima.risk6.database.models.User;

public class SessionManager {

  private static User user;

  public static User getUser() {
    return user;
  }

  public static void setUser(User user) {
    SessionManager.user = user;
  }

  public static void logout() {
    SessionManager.user = null;
  }
}