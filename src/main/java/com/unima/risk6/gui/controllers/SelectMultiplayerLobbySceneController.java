package com.unima.risk6.gui.controllers;

import com.unima.risk6.database.models.User;
import com.unima.risk6.game.models.GameLobby;
import com.unima.risk6.game.models.ServerLobby;
import com.unima.risk6.gui.configurations.SceneConfiguration;
import com.unima.risk6.gui.configurations.SessionManager;
import com.unima.risk6.gui.controllers.enums.SceneName;
import com.unima.risk6.gui.scenes.CreateLobbyScene;
import com.unima.risk6.gui.scenes.MultiplayerLobbyScene;
import com.unima.risk6.gui.scenes.SelectMultiplayerLobbyScene;
import com.unima.risk6.network.configurations.NetworkConfiguration;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;

import static com.unima.risk6.gui.configurations.SoundConfiguration.pauseTitleSound;
import static com.unima.risk6.gui.configurations.StyleConfiguration.applyButtonStyle;
import static com.unima.risk6.gui.configurations.StyleConfiguration.generateBackArrow;

public class SelectMultiplayerLobbySceneController {

  private final SelectMultiplayerLobbyScene selectMultiplayerLobbyScene;
  private final SceneController sceneController;
  private final ServerLobby serverLobby;
  private final SplitPane lobbyChatSplit = new SplitPane();
  private final ListView<String> lobbyList = new ListView<>();
  private User user;
  private BorderPane root;
  private List<GameLobby> gameLobbies = new ArrayList<>();


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
    join.setPrefWidth(400);
    join.setPrefHeight(40);
    join.setAlignment(Pos.CENTER);
    join.setFont(new Font(18));

    join.setOnMouseClicked(e -> handleJoinButton());

    Button create = new Button("Create Lobby");
    applyButtonStyle(create);
    create.setPrefWidth(400);
    create.setPrefHeight(40);
    create.setAlignment(Pos.CENTER);
    create.setFont(new Font(18));

    create.setOnMouseClicked(e -> handleCreateButton());

    Label seperator = new Label(".......");
    seperator.setStyle("-fx-font-size: 1px");
    seperator.setPrefWidth(100);

    HBox bottomBox = new HBox(create, seperator, join);
    bottomBox.setAlignment(Pos.CENTER);

    initSplitPane();

    root.setLeft(backButton);
    root.setTop(titleBox);
    root.setBottom(bottomBox);
    root.setCenter(lobbyChatSplit);

    BorderPane.setMargin(backButton, new Insets(10, 10, 10, 10));
    BorderPane.setMargin(bottomBox, new Insets(10, 20, 20, 10));
    BorderPane.setMargin(titleBox, new Insets(10, 20, 20, 10));
  }

  private void initGameLobbys() {
    //TODO: get all GameLobbys and save them to listview
    gameLobbies = serverLobby.getGameLobbies();
    ObservableList<String> lobbys = FXCollections.observableArrayList();
    for (GameLobby gameLobby : gameLobbies) {
      String temp = gameLobby.getLobbyName() + " hosted by " + gameLobby.getName() + " "
          + gameLobby.getUsers().size() + "/" + gameLobby.getMaxPlayers();
      lobbys.add(temp);
    }
    lobbys.addAll("Lobby 1", "Lobby 2", "Lobby 3");
    lobbyList.setItems(lobbys);
  }

  private void initSplitPane() {

    initGameLobbys();

    TextArea chatArea = new TextArea();
    chatArea.setEditable(false);
    chatArea.setWrapText(true);
    VBox.setVgrow(chatArea, Priority.ALWAYS);

    TextField chatInput = new TextField();
    chatInput.setPromptText("Enter your message...");
    chatInput.setOnKeyPressed(event -> {
      if (event.getCode() == KeyCode.ENTER) {

        String message = chatInput.getText();
        chatInput.clear();

        chatArea.appendText(user.getUsername() + ": " + message + "\n");
        event.consume();
      }
    });

    VBox chatBox = new VBox(chatArea, chatInput);

    lobbyChatSplit.getItems().addAll(lobbyList, chatBox);
    lobbyChatSplit.setDividerPositions(0.6666);
  }


  private void handleJoinButton() {
    MultiplayerLobbyScene scene = (MultiplayerLobbyScene) SceneConfiguration.getSceneController()
        .getSceneBySceneName(SceneName.MULTIPLAYER_LOBBY);
    if (scene == null) {
      scene = new MultiplayerLobbyScene();
      MultiplayerLobbySceneController multiplayerLobbySceneController = new MultiplayerLobbySceneController(
          scene);
      scene.setController(multiplayerLobbySceneController);
      sceneController.addScene(SceneName.MULTIPLAYER_LOBBY, scene);
    }
    pauseTitleSound();
    lobbyChatSplit.getItems().removeAll(lobbyChatSplit.getItems());
    sceneController.activate(SceneName.MULTIPLAYER_LOBBY);
  }

  private void handleCreateButton() {
    //TODO: Create Lobby Scene hinzufügen
    CreateLobbyScene scene = (CreateLobbyScene) SceneConfiguration.getSceneController()
        .getSceneBySceneName(SceneName.CREATE_LOBBY);
    if (scene == null) {
      scene = new CreateLobbyScene();
      CreateLobbySceneController createLobbySceneController = new CreateLobbySceneController(scene);
      scene.setController(createLobbySceneController);
      sceneController.addScene(SceneName.CREATE_LOBBY, scene);
    }
    pauseTitleSound();
    lobbyChatSplit.getItems().removeAll(lobbyChatSplit.getItems());
    sceneController.activate(SceneName.CREATE_LOBBY);
  }
}