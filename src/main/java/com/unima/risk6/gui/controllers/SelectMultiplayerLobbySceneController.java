package com.unima.risk6.gui.controllers;

import static com.unima.risk6.gui.configurations.StyleConfiguration.applyButtonStyle;
import static com.unima.risk6.gui.configurations.StyleConfiguration.generateBackArrow;

import com.unima.risk6.database.models.User;
import com.unima.risk6.game.models.GameLobby;
import com.unima.risk6.game.models.ServerLobby;
import com.unima.risk6.gui.configurations.SceneConfiguration;
import com.unima.risk6.gui.configurations.SessionManager;
import com.unima.risk6.gui.controllers.enums.SceneName;
import com.unima.risk6.gui.scenes.SelectMultiplayerLobbyScene;
import com.unima.risk6.network.configurations.NetworkConfiguration;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;

public class SelectMultiplayerLobbySceneController {

  private final SelectMultiplayerLobbyScene selectMultiplayerLobbyScene;
  private final SceneController sceneController;
  private User user;
  private BorderPane root;
  List<GameLobby> gameLobbies = new ArrayList<>();
  ServerLobby serverLobby;


  public SelectMultiplayerLobbySceneController(
      SelectMultiplayerLobbyScene selectMultiplayerLobbyScene) {
    this.selectMultiplayerLobbyScene = selectMultiplayerLobbyScene;
    this.sceneController = SceneConfiguration.getSceneController();
    this.serverLobby = NetworkConfiguration.getServerLobby();
  }

  public void init() {
    this.user = SessionManager.getUser();
    this.root = (BorderPane) selectMultiplayerLobbyScene.getRoot();
    Font.loadFont(getClass().getResourceAsStream("/com/unima/risk6/Fonts/Fonts/Segoe UI Bold.ttf"),
        26);
    // Initialize elements
    initElements();
  }

  private void initElements() {
    Path arrow = generateBackArrow();

    // Wrap the arrow in a StackPane to handle the click event
    StackPane backButton = new StackPane(arrow);
    backButton.setOnMouseClicked(e -> sceneController.activate(SceneName.TITLE));

    Label title = new Label("Select Multiplayer Lobby");
    title.setAlignment(Pos.CENTER);
    title.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 46px;");

    HBox titleBox = new HBox(title);
    titleBox.setAlignment(Pos.CENTER);

    Button join = new Button("Join Lobby");
    applyButtonStyle(join);
    join.setPrefWidth(470);
    join.setPrefHeight(40);
    join.setAlignment(Pos.CENTER);
    join.setFont(new Font(18));

    HBox joinLobbyButton = new HBox(join);
    joinLobbyButton.setAlignment(Pos.CENTER);

    root.setLeft(backButton);
    root.setTop(titleBox);
    root.setBottom(joinLobbyButton);

    BorderPane.setMargin(backButton, new Insets(10, 0, 0, 10));
    BorderPane.setMargin(joinLobbyButton, new Insets(10, 20, 20, 10));
    BorderPane.setMargin(titleBox, new Insets(10, 20, 20, 10));
  }

  private void initGameLobbys() {
    //TODO: get all GameLobbys and show them on the screen
    gameLobbies = serverLobby.getGameLobbies();
  }

  private void initChat() {
    //TODO: Init Chat
  }
}
