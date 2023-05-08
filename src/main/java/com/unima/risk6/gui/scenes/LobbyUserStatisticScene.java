package com.unima.risk6.gui.scenes;

import com.unima.risk6.game.models.UserDto;
import com.unima.risk6.gui.controllers.LobbyUserStatisticSceneController;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class LobbyUserStatisticScene extends Scene implements InitializableScene {

  private LobbyUserStatisticSceneController lobbyUserStatisticSceneController;

  private UserDto userDto;

  public LobbyUserStatisticScene() {
    super(new BorderPane());
  }

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

