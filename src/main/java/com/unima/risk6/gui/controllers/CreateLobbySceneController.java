package com.unima.risk6.gui.controllers;

import static com.unima.risk6.gui.configurations.StyleConfiguration.applyButtonStyle;

import com.unima.risk6.game.configurations.GameConfiguration;
import com.unima.risk6.game.configurations.LobbyConfiguration;
import com.unima.risk6.game.models.GameLobby;
import com.unima.risk6.gui.configurations.ImageConfiguration;
import com.unima.risk6.gui.configurations.SceneConfiguration;
import com.unima.risk6.gui.configurations.SessionManager;
import com.unima.risk6.gui.configurations.StyleConfiguration;
import com.unima.risk6.gui.controllers.enums.ImageName;
import com.unima.risk6.gui.controllers.enums.SceneName;
import com.unima.risk6.gui.scenes.CreateLobbyScene;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;


public class CreateLobbySceneController {

  private final CreateLobbyScene createLobbyScene;
  private final SceneController sceneController;
  private final String labelStyle = "-fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 26px;";
  ComboBox<String> maxPlayers;
  ComboBox<String> minElo;
  CheckBox chatCheck;
  TextField lobbyNameTextField;
  private BorderPane root;
  private GridPane centerGridPane;
  private GameLobby gameLobby;


  public CreateLobbySceneController(CreateLobbyScene createLobbyScene) {
    this.createLobbyScene = createLobbyScene;
    this.sceneController = SceneConfiguration.getSceneController();
  }

  public void init() {
    this.root = (BorderPane) createLobbyScene.getRoot();
    Font.loadFont(getClass().getResourceAsStream("/com/unima/risk6/fonts/Segoe UI Bold.ttf"), 26);
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
        "-fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 40px;");

    HBox multiplayerSettingsContainer = new HBox(multiplayerSettings);
    multiplayerSettingsContainer.setAlignment(Pos.CENTER);

    initGridpane();

    Button createLobby = new Button("Create Lobby");
    applyButtonStyle(createLobby);

    createLobby.setOnMouseClicked(e -> {
      handleCreateButton();
    });

    createLobby.setPrefSize(1080, 44);
    createLobby.setFont(new Font(20));

    HBox createLobbyContainer = new HBox(createLobby);
    createLobbyContainer.setAlignment(Pos.CENTER);

    VBox vBox = new VBox(multiplayerSettings, centerGridPane, createLobbyContainer);
    vBox.setAlignment(Pos.CENTER);
    vBox.setSpacing(30);

    AnchorPane anchorPane = new AnchorPane();
    anchorPane.setPrefSize(900, 600);
    anchorPane.setPadding(new Insets(60, 100, 60, 100));

    vBox.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 20;"
        + " -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.14), 10, 0, 0, 0); -fx-opacity: 0.9");
    AnchorPane.setTopAnchor(vBox, 0.0);
    AnchorPane.setRightAnchor(vBox, 0.0);
    AnchorPane.setBottomAnchor(vBox, 0.0);
    AnchorPane.setLeftAnchor(vBox, 0.0);

    vBox.setPadding(new Insets(15, 30, 15, 30));

    anchorPane.getChildren().add(vBox);

    // Load the image into an ImageView
    Image originalImage = ImageConfiguration.getImageByName(ImageName.CREATE_LOBBY_BACKGROUND);
    ImageView imageView = new ImageView(originalImage);

// Set the opacity
    imageView.setOpacity(0.9);

// Create a snapshot of the ImageView
    SnapshotParameters parameters = new SnapshotParameters();
    parameters.setFill(Color.TRANSPARENT);
    Image semiTransparentImage = imageView.snapshot(parameters, null);

// Use the semi-transparent image for the background
    BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, true);
    BackgroundImage backgroundImage = new BackgroundImage(semiTransparentImage,
        BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
        backgroundSize);
    Background background = new Background(backgroundImage);
    root.setBackground(background);

    root.setCenter(anchorPane);
    root.setLeft(backButton);
    //root.setTop(multiplayerSettingsContainer);
    //root.setBottom(createLobbyContainer);

    BorderPane.setMargin(backButton, new Insets(10, 10, 10, 10));
    /*BorderPane.setMargin(centerGridPane, new Insets(0, 0, 0, -60));
    BorderPane.setMargin(createLobbyContainer, new Insets(10, 20, 20, 50));
    BorderPane.setMargin(multiplayerSettingsContainer, new Insets(10, 20, 20, 10));*/

  }

  private void handleCreateButton() {
    gameLobby = new GameLobby(lobbyNameTextField.getText(), Integer.parseInt(maxPlayers.getValue()),
        SessionManager.getUser().getUsername(), chatCheck.isSelected(),
        Integer.parseInt(minElo.getValue()),
        GameConfiguration.getMyGameUser());
    gameLobby.getUsers().add(GameConfiguration.getMyGameUser());
    LobbyConfiguration.sendCreateLobby(gameLobby);
  }

  private void initGridpane() {
    centerGridPane = new GridPane();

    Label maxAmountOfPlayers = new Label("Max. Amount of Players: ");
    maxAmountOfPlayers.setStyle(labelStyle);

    Label lobbyName = new Label("Lobby Name: ");
    lobbyName.setStyle(labelStyle);

    Label isChatEnabled = new Label("Enable in-game Chat: ");
    isChatEnabled.setStyle(labelStyle);

    Label matchMakingElo = new Label("Min. required Elo:");
    matchMakingElo.setStyle(labelStyle);

    maxAmountOfPlayers.setMinWidth(350);
    lobbyName.setMinWidth(350);
    isChatEnabled.setMinWidth(350);
    matchMakingElo.setMinWidth(350);

    maxPlayers = new ComboBox<>();
    ObservableList<String> maxPlayersList = FXCollections.observableArrayList();
    maxPlayersList.addAll("2", "3", "4", "5", "6");
    maxPlayers.setItems(maxPlayersList);
    maxPlayers.setPrefWidth(800);

    minElo = new ComboBox<>();
    ObservableList<String> minEloList = FXCollections.observableArrayList();
    minEloList.addAll("0", "1", "2", "3", "4", "5");
    minElo.setItems(minEloList);
    minElo.setPrefWidth(800);

    chatCheck = new CheckBox();

    lobbyNameTextField = new TextField();
    lobbyNameTextField.setPromptText("Enter Lobby Name");
    lobbyNameTextField.setStyle(
        "-fx-font-size: 20; -fx-background-radius: 20; -fx-border-radius: 20;");

    // ComboBox styling
    String comboBoxStyle = "-fx-font-size: 20; -fx-background-color: white; -fx-border-color: "
        + "#cccccc; -fx-border-radius: 20; -fx-background-radius: 20;";
    maxPlayers.setStyle(comboBoxStyle);
    minElo.setStyle(comboBoxStyle);

// CheckBox styling
    String checkBoxBoxStyle = "-fx-font-size: 20; -fx-background-color: white; -fx-border-color: "
        + "#cccccc; -fx-border-radius: 3; -fx-background-radius: 3;";
    String checkBoxSelectedBoxStyle = "-fx-font-size: 20; -fx-background-color: #93d2f8; "
        + "-fx-border-color: #7fc6fd; -fx-border-radius: 3; -fx-background-radius: 3;";

    chatCheck.setStyle(checkBoxBoxStyle);
    chatCheck.selectedProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue) {
        chatCheck.lookup(".box").setStyle(checkBoxSelectedBoxStyle);
      } else {
        chatCheck.lookup(".box").setStyle(checkBoxBoxStyle);
      }
    });

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