package com.unima.risk6.gui.scenes;

import com.unima.risk6.gui.controllers.JoinOnlineSceneController;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

/**
 * The JoinOnlineScene class represents the scene where users can join an online game.
 * It implements the InitializableScene interface and utilizes the JoinOnlineSceneController
 * for managing the UI elements and actions within the scene.
 *
 * @author fisommer
 */

public class JoinOnlineScene extends Scene implements InitializableScene {

  private JoinOnlineSceneController joinOnlineSceneController;

  /**
   * Constructor for JoinOnlineScene. It initializes a new scene with a BorderPane layout.
   */

  public JoinOnlineScene() {
    super(new BorderPane());
  }

  /**
   * Initializes the scene by calling the controller's init method if the controller is not null.
   */

  @Override
  public void init() {
    if (joinOnlineSceneController != null) {
      joinOnlineSceneController.init();
    }
  }

  public void setController(JoinOnlineSceneController joinOnlineSceneController) {
    this.joinOnlineSceneController = joinOnlineSceneController;
  }
}
