package com.unima.risk6.gui.scenes;

import com.unima.risk6.database.models.User;
import com.unima.risk6.gui.controllers.UserOptionsSceneController;
import com.unima.risk6.gui.controllers.UserStatisticsSceneController;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class UserStatisticsScene extends Scene implements InitializableScene {
  private UserStatisticsSceneController userStatisticsSceneController;
  private User user;

  public UserStatisticsScene() {
    super(new BorderPane());
  }

  @Override
  public void init() {
    if (userStatisticsSceneController != null) {
      userStatisticsSceneController.init(user);
    }
  }

  public void setController(UserStatisticsSceneController userStatisticsSceneController) {
    this.userStatisticsSceneController = userStatisticsSceneController;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }
}

