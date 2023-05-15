package com.unima.risk6.gui.scenes;

import com.unima.risk6.game.models.UserDto;
import com.unima.risk6.gui.controllers.LobbyUserStatisticSceneController;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

/**
 * The LobbyUserStatisticScene class represents the scene where users can view game statistics for a
 * specific user in the game lobby. It implements the InitializableScene interface and utilizes the
 * LobbyUserStatisticSceneController for managing the UI elements and actions within the scene.
 *
 * @author fisommer
 */

public class LobbyUserStatisticScene extends Scene implements InitializableScene {

  private LobbyUserStatisticSceneController lobbyUserStatisticSceneController;

  private UserDto userDto;

  /**
   * Constructor for LobbyUserStatisticScene. It initializes a new scene with a BorderPane layout.
   */

  public LobbyUserStatisticScene() {
    super(new BorderPane());
  }

  /**
   * Initializes the scene by calling the controller's init method if the controller is not null.
   * The controller's init method is provided with the userDto data.
   */

  @Override
  public void init() {
    if (lobbyUserStatisticSceneController != null) {
      lobbyUserStatisticSceneController.init(userDto);
    }
  }

  public void setController(LobbyUserStatisticSceneController lobbyUserStatisticSceneController) {
    this.lobbyUserStatisticSceneController = lobbyUserStatisticSceneController;
  }

  public void setUserDto(UserDto userDto) {
    this.userDto = userDto;
  }
}

