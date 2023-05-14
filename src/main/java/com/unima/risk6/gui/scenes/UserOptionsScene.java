package com.unima.risk6.gui.scenes;

import com.unima.risk6.gui.controllers.UserOptionsSceneController;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

/**
 * The UserOptionsScene class represents the scene for user options in the game. It implements the
 * InitializableScene interface, indicating that it can be initialized. It includes methods to set
 * the controller for the scene and to initialize the scene.
 *
 * @author fisommer
 */
public class UserOptionsScene extends Scene implements InitializableScene {

  /**
   * The controller for this scene.
   */
  private UserOptionsSceneController userOptionsSceneController;

  /**
   * Constructor for UserOptionsScene. It sets a new BorderPane as its root.
   */
  public UserOptionsScene() {
    super(new BorderPane());
  }

  /**
   * Initializes the scene. It calls the initialization method on the controller, if the controller
   * is not null.
   */
  @Override
  public void init() {
    if (userOptionsSceneController != null) {
      userOptionsSceneController.init();
    }
  }

  public void setController(UserOptionsSceneController userOptionsSceneController) {
    this.userOptionsSceneController = userOptionsSceneController;
  }
}

