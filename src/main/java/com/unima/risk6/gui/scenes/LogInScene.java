package com.unima.risk6.gui.scenes;

import com.unima.risk6.gui.controllers.LoginSceneController;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;

public class LogInScene extends Scene implements InitializableScene {

  public LoginSceneController loginSceneController;

  public LogInScene() {
    super(new VBox());
  }

  @Override
  public void init() {
    if (loginSceneController != null) {
      this.setRoot(new VBox());
      loginSceneController.init();
    }
  }

  public void setLoginSceneController(
      LoginSceneController loginSceneController) {
    this.loginSceneController = loginSceneController;
  }
}
