package com.unima.risk6.gui.controllers;

import static com.unima.risk6.gui.configurations.SoundConfiguration.pauseTitleSound;
import static com.unima.risk6.gui.configurations.StyleConfiguration.generateBackArrow;

import com.unima.risk6.gui.configurations.SceneConfiguration;
import com.unima.risk6.gui.controllers.enums.SceneName;
import com.unima.risk6.gui.scenes.JoinOnlineScene;
import com.unima.risk6.gui.scenes.MultiplayerLobbyScene;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
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
    anchorPane.setPadding(new Insets(150, 250, 150, 250));

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

    TextField usernameField = new TextField();
    usernameField.setPrefSize(470, 38);
    usernameField.setFont(Font.font(18));
    usernameField.setPromptText("Enter IP Adress");
    usernameField.setStyle("-fx-background-radius: 20; -fx-border-radius: 20;");

    PasswordField passwordField = new PasswordField();
    passwordField.setPrefSize(470, 39);
    passwordField.setFont(Font.font(18));
    passwordField.setPromptText("Enter Port");
    passwordField.setStyle("-fx-background-radius: 20; -fx-border-radius: 20;");

    PasswordField checkPasswordField = new PasswordField();
    checkPasswordField.setPrefSize(470, 40);
    checkPasswordField.setFont(Font.font(18));
    checkPasswordField.setPromptText("Enter Password again");
    checkPasswordField.setStyle("-fx-background-radius: 20; -fx-border-radius: 20;");

    Label passwordMismatchLabel = new Label();
    passwordMismatchLabel.setFont(Font.font(14));
    passwordMismatchLabel.setTextFill(Color.RED);
    passwordMismatchLabel.setPadding(new Insets(-10, 0, -10, 0));

    Button createButton = new Button("Join");
    createButton.setPrefSize(470, 40);
    createButton.setFont(Font.font(18));
    createButton.setStyle(
        "-fx-background-color: linear-gradient(#FFDAB9, #FFA07A); -fx-text-fill: #FFFFFF; -fx-background-radius: 20; -fx-border-radius: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.14), 10, 0, 0, 0);");

    Label loginLabel = new Label("Login to existing account");
    loginLabel.setFont(Font.font(24));
    loginLabel.setStyle(
        "-fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 14px; -fx-text-fill: #BEBEBE; -fx-underline: true;");
// Add event handlers for the loginLabel if necessary, e.g.:
// loginLabel.setOnMouseClicked(event -> handleSkipToTitleScreen());
// loginLabel.setOnMouseEntered(event -> handleMouseEntered());
// loginLabel.setOnMouseExited(event -> handleMouseExited());
    vBox.getChildren()
        .addAll(titleLabel, usernameField, passwordField, checkPasswordField, passwordMismatchLabel,
            createButton, loginLabel);
    vBox.setPadding(new Insets(15, 15, 15, 15));

    anchorPane.getChildren().add(vBox);

    createButton.setOnMouseClicked(e -> handleCreate());

    return anchorPane;
  }
// Add your event handler methods here, e.g.:
// private void handleSkipToTitleScreen() { /* ... */ }
// private void handleMouseEntered() { /* ... */ }
// private void handleMouseExited() { /* ... */ }

  private void handleCreate() {
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
