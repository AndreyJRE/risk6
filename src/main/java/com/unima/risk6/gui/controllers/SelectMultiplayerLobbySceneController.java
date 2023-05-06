package com.unima.risk6.gui.controllers;

import static com.unima.risk6.gui.configurations.SoundConfiguration.pauseTitleSound;
import static com.unima.risk6.gui.configurations.StyleConfiguration.applyButtonStyle;
import static com.unima.risk6.gui.configurations.StyleConfiguration.generateBackArrow;

import com.unima.risk6.database.models.User;
import com.unima.risk6.game.models.GameLobby;
import com.unima.risk6.game.models.ServerLobby;
import com.unima.risk6.gui.configurations.SceneConfiguration;
import com.unima.risk6.gui.configurations.SessionManager;
import com.unima.risk6.gui.controllers.enums.SceneName;
import com.unima.risk6.gui.scenes.MultiplayerLobbyScene;
import com.unima.risk6.gui.scenes.SelectMultiplayerLobbyScene;
import com.unima.risk6.network.configurations.NetworkConfiguration;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;

public class SelectMultiplayerLobbySceneController {

  private final SelectMultiplayerLobbyScene selectMultiplayerLobbyScene;
  private final SceneController sceneController;
  private User user;
  private BorderPane root;
  private List<GameLobby> gameLobbies = new ArrayList<>();
  private ServerLobby serverLobby;
  private SplitPane lobbyChatSplit = new SplitPane();


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

    join.setOnMouseClicked(e -> handleJoinButton());

    initSplitPane();

    root.setLeft(backButton);
    root.setTop(titleBox);
    root.setBottom(joinLobbyButton);
    root.setCenter(lobbyChatSplit);

    BorderPane.setMargin(backButton, new Insets(10, 0, 0, 10));
    BorderPane.setMargin(joinLobbyButton, new Insets(10, 20, 20, 10));
    BorderPane.setMargin(titleBox, new Insets(10, 20, 20, 10));
  }

  private void initGameLobbys() {
    //TODO: get all GameLobbys and save them to listview
    gameLobbies = serverLobby.getGameLobbies();
  }

  private void initSplitPane() {

    ListView<String> lobbyList = new ListView<>();
    ObservableList<String> items = FXCollections.observableArrayList();

    items.addAll("Lobby 1", "Lobby 2", "Lobby 3");
    lobbyList.setItems(items);

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
}
