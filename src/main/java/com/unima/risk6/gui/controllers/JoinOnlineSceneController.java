package com.unima.risk6.gui.controllers;

import static com.unima.risk6.gui.configurations.SoundConfiguration.pauseTitleSound;
import static com.unima.risk6.gui.configurations.StyleConfiguration.generateBackArrow;

import com.unima.risk6.database.configurations.DatabaseConfiguration;
import com.unima.risk6.game.configurations.LobbyConfiguration;
import com.unima.risk6.game.models.UserDto;
import com.unima.risk6.gui.configurations.SceneConfiguration;
import com.unima.risk6.gui.configurations.SessionManager;
import com.unima.risk6.gui.controllers.enums.SceneName;
import com.unima.risk6.gui.scenes.JoinOnlineScene;
import com.unima.risk6.gui.scenes.MultiplayerLobbyScene;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;

public class JoinOnlineSceneController {

  private final JoinOnlineScene joinOnlineScene;
  private final SceneController sceneController;
  private BorderPane root;

  public JoinOnlineSceneController(JoinOnlineScene joinOnlineScene) {
    this.joinOnlineScene = joinOnlineScene;
    this.sceneController = SceneConfiguration.getSceneController();
  }

  public void init() {
    this.root = (BorderPane) joinOnlineScene.getRoot();
    Font.loadFont(getClass().getResourceAsStream("/com/unima/risk6/Fonts/Fonts/Segoe UI Bold.ttf"),
        26);
    initElements();
  }

  private void initElements() {
    AnchorPane centralBox = initCentralVBox();
    // Back arrow
    Path arrow = generateBackArrow();

    // Wrap the arrow in a StackPane to handle the click event
    StackPane backButton = new StackPane(arrow);
    backButton.setOnMouseClicked(e -> sceneController.activate(SceneName.TITLE));

    root.setCenter(centralBox);
    root.setLeft(backButton);

    BorderPane.setMargin(backButton, new Insets(10, 0, 0, 10));
  }

  private AnchorPane initCentralVBox() {

    HBox hBox = new HBox();
    hBox.setAlignment(Pos.CENTER);

    AnchorPane anchorPane = new AnchorPane();
    anchorPane.setPrefSize(600, 495);
    anchorPane.setPadding(new Insets(190, 270, 190, 270));

    VBox vBox = new VBox();
    vBox.setAlignment(Pos.CENTER);
    vBox.setSpacing(22);
    vBox.setStyle(
        "-fx-background-color: #FFFFFF; -fx-background-radius: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.14), 10, 0, 0, 0);");
    AnchorPane.setTopAnchor(vBox, 0.0);
    AnchorPane.setRightAnchor(vBox, 0.0);
    AnchorPane.setBottomAnchor(vBox, 0.0);
    AnchorPane.setLeftAnchor(vBox, 0.0);

    Label titleLabel = new Label("Join Online");
    titleLabel.setFont(Font.font("BentonSans Bold", 41));
    titleLabel.setStyle(
        "-fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #2D2D2D;");
    //TODO Change to normal names, not username and password
    TextField usernameField = new TextField();
    usernameField.setPrefSize(330, 38);
    usernameField.setFont(Font.font(18));
    usernameField.setPromptText("Enter IP Adress");
    usernameField.setStyle("-fx-background-radius: 20; -fx-border-radius: 20;");

    PasswordField passwordField = new PasswordField();
    passwordField.setPrefSize(330, 39);
    passwordField.setFont(Font.font(18));
    passwordField.setPromptText("Enter Port");
    passwordField.setStyle("-fx-background-radius: 20; -fx-border-radius: 20;");

    Label passwordMismatchLabel = new Label();
    passwordMismatchLabel.setFont(Font.font(14));
    passwordMismatchLabel.setTextFill(Color.RED);
    passwordMismatchLabel.setPadding(new Insets(-10, 0, -10, 0));

    Button createButton = new Button("Join");
    createButton.setPrefSize(500, 40);
    createButton.setFont(Font.font(18));
    createButton.setStyle(
        "-fx-background-color: linear-gradient(#FFDAB9, #FFA07A); -fx-text-fill: #FFFFFF; -fx-background-radius: 20; -fx-border-radius: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.14), 10, 0, 0, 0);");

    Label ipLabel = new Label("Enter IP Adress:");
    ipLabel.setFont(Font.font(18));
    ipLabel.setAlignment(Pos.CENTER_LEFT);

    Label portLabel = new Label("Enter Port:");
    portLabel.setFont(Font.font(18));
    portLabel.setAlignment(Pos.CENTER_LEFT);

    GridPane centerGrid = new GridPane();
    centerGrid.add(ipLabel, 0, 0);
    centerGrid.add(portLabel, 0, 1);
    centerGrid.add(usernameField, 1, 0);
    centerGrid.add(passwordField, 1, 1);
    centerGrid.setHgap(20);
    centerGrid.setVgap(15);

    vBox.getChildren().addAll(titleLabel, centerGrid, passwordMismatchLabel, createButton);
    vBox.setPadding(new Insets(15, 15, 15, 15));

    anchorPane.getChildren().add(vBox);

    createButton.setOnMouseClicked(
        e -> handleCreate(usernameField.getText(), Integer.parseInt(passwordField.getText())));

    return anchorPane;
  }
// Add your event handler methods here, e.g.:
// private void handleSkipToTitleScreen() { /* ... */ }
// private void handleMouseEntered() { /* ... */ }
// private void handleMouseExited() { /* ... */ }

  private void handleCreate(String host, int port) {
    LobbyConfiguration.configureGameClient(host, port);
    LobbyConfiguration.startGameClient();
    UserDto userDto = UserDto.mapUserAndHisGameStatistics(SessionManager.getUser(),
        DatabaseConfiguration.getGameStatisticService()
            .getAllStatisticsByUserId(SessionManager.getUser().getId()));
    Platform.runLater(() -> LobbyConfiguration.sendJoinServer(userDto));
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
  }


}
