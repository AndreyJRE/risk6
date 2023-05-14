package com.unima.risk6.gui.scenes;

import com.unima.risk6.gui.controllers.SelectedUserSceneController;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

/**
 * A scene for a user to log in to his account.
 *
 * @author fisommer
 */

public class SelectedUserScene extends Scene implements InitializableScene {

  private SelectedUserSceneController selectedUserSceneController;

  /**
   * Constructs a new SelectedUserScene with a BorderPane as its root.
   */

  public SelectedUserScene() {
    super(new BorderPane());
  }

  /**
   * Initializes the scene if the controller is present.
   */

  @Override
  public void init() {
    if (selectedUserSceneController != null) {
      selectedUserSceneController.init();
    }
  }

  public void setController(
      SelectedUserSceneController selectedUserSceneController) {
    this.selectedUserSceneController = selectedUserSceneController;
  }
}
