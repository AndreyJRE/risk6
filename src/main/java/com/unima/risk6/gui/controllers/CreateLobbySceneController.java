package com.unima.risk6.gui.controllers;

import static com.unima.risk6.gui.configurations.SoundConfiguration.pauseTitleSound;
import static com.unima.risk6.gui.configurations.StyleConfiguration.applyButtonStyle;

import com.unima.risk6.gui.configurations.SceneConfiguration;
import com.unima.risk6.gui.configurations.StyleConfiguration;
import com.unima.risk6.gui.controllers.enums.SceneName;
import com.unima.risk6.gui.scenes.CreateLobbyScene;
import com.unima.risk6.gui.scenes.MultiplayerLobbyScene;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;


public class CreateLobbySceneController {

  private final CreateLobbyScene createLobbyScene;
  private final SceneController sceneController;
  private final String labelStyle = "-fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 26px;";
  private BorderPane root;
  private GridPane centerGridPane;


  public CreateLobbySceneController(CreateLobbyScene createLobbyScene) {
    this.createLobbyScene = createLobbyScene;
    this.sceneController = SceneConfiguration.getSceneController();
  }

  public void init() {
    this.root = (BorderPane) createLobbyScene.getRoot();
    Font.loadFont(getClass().getResourceAsStream("/com/unima/risk6/Fonts/Fonts/Segoe UI Bold.ttf"),
        26);
    // Initialize elements
    initElements();
  }

  private void initElements() {
    // Back arrow
    Path arrow = StyleConfiguration.generateBackArrow();

    // Wrap the arrow in a StackPane to handle the click event
    StackPane backButton = new StackPane(arrow);
    backButton.setOnMouseClicked(e -> sceneController.activate(SceneName.SELECT_LOBBY));

    Label multiplayerSettings = new Label("Multiplayer Settings");
    multiplayerSettings.setAlignment(Pos.CENTER);
    multiplayerSettings.setStyle(
        "-fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 70px;");

    HBox multiplayerSettingsContainer = new HBox(multiplayerSettings);
    multiplayerSettingsContainer.setAlignment(Pos.CENTER);

    initGridpane();

    Button createLobby = new Button("Create Lobby");
    applyButtonStyle(createLobby);

    createLobby.setOnMouseClicked(e -> {
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
      sceneController.activate(SceneName.MULTIPLAYER_LOBBY);
    });

    createLobby.setPrefSize(600, 44);
    createLobby.setFont(new Font(20));

    HBox createLobbyContainer = new HBox(createLobby);
    createLobbyContainer.setAlignment(Pos.CENTER);

    root.setCenter(centerGridPane);
    root.setLeft(backButton);
    root.setTop(multiplayerSettingsContainer);
    root.setBottom(createLobbyContainer);

    BorderPane.setMargin(backButton, new Insets(10, 10, 10, 10));
    BorderPane.setMargin(centerGridPane, new Insets(0, 0, 0, -60));
    BorderPane.setMargin(createLobbyContainer, new Insets(10, 20, 20, 50));
    BorderPane.setMargin(multiplayerSettingsContainer, new Insets(10, 20, 20, 10));

  }

  private void initGridpane() {
    //TODO: AnchorPane darum machen für schöneres Design

    centerGridPane = new GridPane();

    Label maxAmountOfPlayers = new Label("Max. Amount of Players: ");
    maxAmountOfPlayers.setStyle(labelStyle);

    Label lobbyName = new Label("Lobby Name: ");
    lobbyName.setStyle(labelStyle);

    Label isChatEnabled = new Label("Enable in-game Chat: ");
    isChatEnabled.setStyle(labelStyle);

    Label turnTime = new Label("Turn Time: ");
    turnTime.setStyle(labelStyle);

    Label matchMakingElo = new Label("Min. required Elo:");
    matchMakingElo.setStyle(labelStyle);

    ComboBox<String> turnTimeBox = new ComboBox<>();
    ObservableList<String> turnTimes = FXCollections.observableArrayList();
    turnTimes.addAll("60 Seconds ", "90 Seconds", "120 Seconds", "150 Seconds", "180 Seconds",
        "300 Seconds");
    turnTimeBox.setItems(turnTimes);

    ComboBox<String> maxPlayers = new ComboBox<>();
    ObservableList<String> maxPlayersList = FXCollections.observableArrayList();
    maxPlayersList.addAll("2", "3", "4", "5", "6");
    maxPlayers.setItems(maxPlayersList);

    ComboBox<String> minElo = new ComboBox<>();
    ObservableList<String> minEloList = FXCollections.observableArrayList();
    minEloList.addAll("0", "500", "1000", "1500", "2000");
    minElo.setItems(maxPlayersList);

    CheckBox chatCheck = new CheckBox();

    TextField lobbyNameTextField = new TextField();
    lobbyNameTextField.setPromptText("Enter Lobby Name");

    centerGridPane.add(lobbyName, 0, 0);
    centerGridPane.add(maxAmountOfPlayers, 0, 1);
    centerGridPane.add(lobbyNameTextField, 1, 0);
    centerGridPane.add(maxPlayers, 1, 1);
    centerGridPane.add(matchMakingElo, 0, 2);
    centerGridPane.add(minElo, 1, 2);
    centerGridPane.add(isChatEnabled, 0, 3);
    centerGridPane.add(chatCheck, 1, 3);
    centerGridPane.setAlignment(Pos.CENTER);
    centerGridPane.setHgap(60); // Set horizontal gap
    centerGridPane.setVgap(40); // Set vertical gap
  }

}