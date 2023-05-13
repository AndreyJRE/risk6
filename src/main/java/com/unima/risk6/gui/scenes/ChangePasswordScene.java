package com.unima.risk6.gui.scenes;

import com.unima.risk6.gui.controllers.ChangePasswordSceneController;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
/**
 * Represents the scene where the user can change their password.
 *
 * @author: astoyano
 */
public class ChangePasswordScene extends Scene implements InitializableScene {

  private ChangePasswordSceneController changePasswordSceneController;
  /**
   * Constructs a ChangePasswordScene with a BorderPane as its root.
   */
  public ChangePasswordScene() {
    super(new BorderPane());
  }
  /**
   * Initializes the scene. If a controller has been set, its initialization method is called.
   */
  @Override
  public void init() {
    if (changePasswordSceneController != null) {
      changePasswordSceneController.init();
    }
  }
  /**
   * Sets the controller for this scene.
   *
   * @param changePasswordSceneController the controller to be set for this scene.
   */
  public void setController(
      ChangePasswordSceneController changePasswordSceneController) {
    this.changePasswordSceneController = changePasswordSceneController;
  }
}
