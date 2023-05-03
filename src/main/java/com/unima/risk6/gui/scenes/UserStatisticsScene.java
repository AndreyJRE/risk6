package com.unima.risk6.gui.scenes;

import com.unima.risk6.gui.controllers.UserStatisticsSceneController;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class UserStatisticsScene extends Scene implements InitializableScene {

  private UserStatisticsSceneController userStatisticsSceneController;

  public UserStatisticsScene() {
    super(new BorderPane());
  }

  @Override
  public void init() {
    if (userStatisticsSceneController != null) {
      userStatisticsSceneController.init();
    }
  }

  public void setController(UserStatisticsSceneController userStatisticsSceneController) {
    this.userStatisticsSceneController = userStatisticsSceneController;
  }

}

