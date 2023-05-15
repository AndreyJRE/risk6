package com.unima.risk6.gui.scenes;

import com.unima.risk6.gui.controllers.CreateLobbySceneController;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

/**
 * The CreateLobbyScene class represents the scene where a new game lobby is created. It implements
 * the InitializableScene interface and uses CreateLobbySceneController for managing the UI elements
 * and actions within the scene.
 *
 * @author fisommer
 */

public class CreateLobbyScene extends Scene implements InitializableScene {

  private CreateLobbySceneController createLobbySceneController;

  /**
   * Constructor for CreateLobbyScene. It initializes a new scene with a BorderPane layout.
   */

  public CreateLobbyScene() {
    super(new BorderPane());
  }

  /**
   * Initializes the scene by calling the controller's init method if the controller is not null.
   */

  @Override
  public void init() {
    if (createLobbySceneController != null) {
      createLobbySceneController.init();
    }
  }

  public void setController(CreateLobbySceneController createLobbySceneController) {
    this.createLobbySceneController = createLobbySceneController;
  }

}

