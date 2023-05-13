package com.unima.risk6.gui.scenes;

import com.unima.risk6.gui.controllers.LoginSceneController;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
/**
 * Represents the scene where the user can log in.
 *
 * @author fisommer
 */
public class LogInScene extends Scene implements InitializableScene {

  public LoginSceneController loginSceneController;
  /**
   * Constructs a LogInScene with a VBox as its root.
   */
  public LogInScene() {
    super(new VBox());
  }
  /**
   * Initializes the scene. If a controller has been set, its initialization method is called.
   */
  @Override
  public void init() {
    if (loginSceneController != null) {
      this.setRoot(new VBox());
      loginSceneController.init();
    }
  }
  /**
   * Sets the controller for this scene.
   *
   * @param loginSceneController the controller to be set for this scene.
   */
  public void setLoginSceneController(
      LoginSceneController loginSceneController) {
    this.loginSceneController = loginSceneController;
  }
}
