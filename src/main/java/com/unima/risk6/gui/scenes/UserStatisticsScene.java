package com.unima.risk6.gui.scenes;

import com.unima.risk6.gui.controllers.UserStatisticsSceneController;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

/**
 * The UserStatisticsScene class represents the scene for user statistics in the application.
 * It extends the JavaFX Scene class and implements InitializableScene interface.
 *
 * @author fisommer
 */

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

