package com.unima.risk6.gui.scenes;

import com.unima.risk6.database.models.User;
import com.unima.risk6.gui.controllers.SinglePlayerSettingsSceneController;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class SinglePlayerSettingsScene extends Scene implements InitializableScene {
  private SinglePlayerSettingsSceneController singlePlayerSettingsSceneController;
  private User user;

  public SinglePlayerSettingsScene() {
    super(new AnchorPane());
  }

  @Override
  public void init() {
    if (singlePlayerSettingsSceneController != null) {
      singlePlayerSettingsSceneController.init(user);
    }
  }

  public void setController(SinglePlayerSettingsSceneController singlePlayerSettingsSceneController) {
    this.singlePlayerSettingsSceneController = singlePlayerSettingsSceneController;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }
}

