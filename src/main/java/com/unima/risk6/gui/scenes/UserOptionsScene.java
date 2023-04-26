package com.unima.risk6.gui.scenes;

import com.unima.risk6.database.models.User;
import com.unima.risk6.gui.controllers.UserOptionsSceneController;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class UserOptionsScene extends Scene implements InitializableScene {
  private UserOptionsSceneController userOptionsSceneController;
  private User user;

  public UserOptionsScene() {
    super(new BorderPane());
  }

  @Override
  public void init() {
    if (userOptionsSceneController != null) {
      userOptionsSceneController.init(user);
    }
  }

  public void setController(UserOptionsSceneController userOptionsSceneController) {
    this.userOptionsSceneController = userOptionsSceneController;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }
}

