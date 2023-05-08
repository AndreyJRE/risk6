package com.unima.risk6.gui.controllers;

import static com.unima.risk6.gui.configurations.SoundConfiguration.pauseTitleSound;
import static com.unima.risk6.gui.configurations.StyleConfiguration.applyButtonStyle;
import static com.unima.risk6.gui.configurations.StyleConfiguration.generateBackArrow;
import static com.unima.risk6.gui.configurations.StyleConfiguration.showConfirmationDialog;
import static com.unima.risk6.gui.configurations.StyleConfiguration.showErrorDialog;

import com.unima.risk6.game.configurations.GameConfiguration;
import com.unima.risk6.game.configurations.LobbyConfiguration;
import com.unima.risk6.game.configurations.observers.ServerLobbyObserver;
import com.unima.risk6.game.models.GameLobby;
import com.unima.risk6.game.models.ServerLobby;
import com.unima.risk6.game.models.UserDto;
import com.unima.risk6.gui.configurations.SceneConfiguration;
import com.unima.risk6.gui.controllers.enums.SceneName;
import com.unima.risk6.gui.scenes.CreateLobbyScene;
import com.unima.risk6.gui.scenes.MultiplayerLobbyScene;
import com.unima.risk6.gui.scenes.SelectMultiplayerLobbyScene;
import java.util.Iterator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
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

public class SelectMultiplayerLobbySceneController implements ServerLobbyObserver {

  private final SelectMultiplayerLobbyScene selectMultiplayerLobbyScene;
  private final SceneController sceneController;
  private final SplitPane lobbyChatSplit = new SplitPane();
  private final ListView<GameLobby> lobbyList = new ListView<>();
  private ServerLobby serverLobby;
  private BorderPane root;

  private UserDto user;
  private ObservableList<GameLobby> lobbies;


  public SelectMultiplayerLobbySceneController(
      SelectMultiplayerLobbyScene selectMultiplayerLobbyScene) {
    this.selectMultiplayerLobbyScene = selectMultiplayerLobbyScene;
    this.sceneController = SceneConfiguration.getSceneController();
    LobbyConfiguration.addServerLobbyObserver(this);
  }

  public void init() {
    this.serverLobby = LobbyConfiguration.getServerLobby();
    this.user = GameConfiguration.getMyGameUser();
    this.root = (BorderPane) selectMultiplayerLobbyScene.getRoot();
    Font.loadFont(getClass().getResourceAsStream("/com/unima/risk6/fonts/Segoe UI Bold.ttf"),
        26);
    for (int i = lobbyChatSplit.getItems().size(); i > 0; i--) {
      lobbyChatSplit.getItems().remove(i - 1);
    }
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
    lobbies = FXCollections.observableArrayList();
    lobbies.addAll(serverLobby.getGameLobbies());
    String listViewStyle = "-fx-background-color: transparent;" // Hintergrundfarbe der ListView
        + "-fx-control-inner-background: #f0f0f0;" // Hintergrundfarbe der Zellen
        + "-fx-control-inner-background-alt: #e0e0e0;" // Alternierende Hintergrundfarbe der Zellen
        + "-fx-font-size: 20;" // Schriftgröße
        + "-fx-border-color: #cccccc;" // Randfarbe der ListView
        + "-fx-cell-size: 80;" // Zellengröße, um mehr Platz zwischen den Einträgen zu schaffen
        + "-fx-wrap-text: true";

    lobbyList.setStyle(listViewStyle);
    lobbyList.setCellFactory(param -> new ListCell<GameLobby>() {
      @Override
      protected void updateItem(GameLobby item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
          setText(null);
          setGraphic(null);
        } else {
          setText(
              item.getLobbyName() + "  hosted by " + item.getName() + "              "
                  + item.getUsers().size()
                  + "/" + item.getMaxPlayers() + " Players" + "              min. Elo: "
                  + item.getMatchMakingElo());
          setPadding(new Insets(20, 30, 20, 30)); // Padding für die Zellen
        }
      }
    });

    lobbyList.setItems(lobbies);
  }

  private void initSplitPane() {
    initGameLobbys();

    //TODO: Implement Chat Server Integration
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
   /* if (lobbyList.getSelectionModel().getSelectedIndex() == -1) {
    } else {
      GameLobby gameLobby = gameLobbies.get(lobbyList.getSelectionModel().getSelectedIndex());
      MultiplayerLobbyScene scene = (MultiplayerLobbyScene) SceneConfiguration.getSceneController()
          .getSceneBySceneName(SceneName.MULTIPLAYER_LOBBY);
      if (scene == null) {
        scene = new MultiplayerLobbyScene();
        MultiplayerLobbySceneController multiplayerLobbySceneController = new MultiplayerLobbySceneController(
            scene, gameLobby);
        scene.setController(multiplayerLobbySceneController);
        sceneController.addScene(SceneName.MULTIPLAYER_LOBBY, scene);
      }
      pauseTitleSound();
      lobbyChatSplit.getItems().removeAll(lobbyChatSplit.getItems());
      sceneController.activate(SceneName.MULTIPLAYER_LOBBY);
    }*/
    //TODO For test purposes
    if (lobbyList.getSelectionModel().getSelectedItem() != null) {
      if (lobbyList.getSelectionModel().getSelectedItem().getMatchMakingElo()
          <= GameConfiguration.getMyGameUser().getWinLossRatio()) {
        LobbyConfiguration.sendJoinLobby(lobbyList.getSelectionModel().getSelectedItem());
        //lobbyList.getSelectionModel().getSelectedItem().addUser(GameConfiguration.getMyGameUser());
        MultiplayerLobbyScene scene = (MultiplayerLobbyScene) SceneConfiguration.getSceneController()
            .getSceneBySceneName(SceneName.MULTIPLAYER_LOBBY);
        if (scene == null) {
          scene = new MultiplayerLobbyScene();
          //TODO Overwrite game lobby owner
          MultiplayerLobbySceneController multiplayerLobbySceneController = new MultiplayerLobbySceneController(
              scene);
          scene.setController(multiplayerLobbySceneController);
          sceneController.addScene(SceneName.MULTIPLAYER_LOBBY, scene);
        }
        pauseTitleSound();
        lobbyChatSplit.getItems().removeAll(lobbyChatSplit.getItems());
        sceneController.activate(SceneName.MULTIPLAYER_LOBBY);
      } else {
        showConfirmationDialog("Missing experience",
            "Your Win / Loss Ratio does not match the minimum required"
                + " Ratio of the selected Lobby. Do you still want to continue?");
      }
    } else {
      showErrorDialog("No Lobby selected", "Please select a Lobby in order to join.");
    }
  }


  private void handleCreateButton() {
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

  @Override
  public void updateServerLobby(ServerLobby serverLobby) {
    this.serverLobby = serverLobby;
    Iterator<GameLobby> iterator = lobbies.listIterator();
    while (iterator.hasNext()) {
      iterator.next();
      iterator.remove();
    }
    lobbies.addAll(serverLobby.getGameLobbies());
  }
}
