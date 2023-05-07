package com.unima.risk6.gui.controllers;

import static com.unima.risk6.gui.configurations.SoundConfiguration.pauseTitleSound;
import static com.unima.risk6.gui.configurations.StyleConfiguration.applyButtonStyle;

import com.unima.risk6.database.configurations.DatabaseConfiguration;
import com.unima.risk6.database.services.UserService;
import com.unima.risk6.gui.configurations.SceneConfiguration;
import com.unima.risk6.gui.controllers.enums.SceneName;
import com.unima.risk6.gui.scenes.JoinOnlineScene;
import com.unima.risk6.gui.scenes.SinglePlayerSettingsScene;
import com.unima.risk6.gui.scenes.UserOptionsScene;
import com.unima.risk6.network.configurations.NetworkConfiguration;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.FillTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class TitleSceneController implements Initializable {

  @FXML
  private AnchorPane root;

  @FXML
  private ImageView backgroundImageView;

  @FXML
  private VBox vBox;

  @FXML
  private HBox hBox;

  @FXML
  private VBox titleBox;

  @FXML
  private Label titleLabel;

  @FXML
  private Button singlePlayerButton;

  @FXML
  private Button multiPlayerButton;

  @FXML
  private Button optionsButton;

  @FXML
  private Button quitButton;
  @FXML
  private Rectangle background;

  @FXML
  private Circle trigger;

  private BooleanProperty switchedOn = new SimpleBooleanProperty(false);
  private TranslateTransition translateAnimation = new TranslateTransition(Duration.seconds(0.25));
  private FillTransition fillAnimation = new FillTransition(Duration.seconds(0.25));
  private ParallelTransition animation = new ParallelTransition(translateAnimation, fillAnimation);

  public BooleanProperty switchedOnProperty() {
    return switchedOn;
  }


  private SceneController sceneController;
  private UserService userService;

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    userService = DatabaseConfiguration.getUserService();
    // Set the font of the title label
    titleLabel.setFont(Font.font("72 Bold Italic", 96.0));
    DropShadow dropShadow = new DropShadow();
    dropShadow.setRadius(10.0);
    dropShadow.setOffsetX(3.0);
    dropShadow.setOffsetY(3.0);
    dropShadow.setColor(Color.BLACK);
    titleLabel.setEffect(dropShadow);
    animateTitleLabel();
    root.setPrefHeight(SceneConfiguration.getHeight());
    root.setPrefWidth(SceneConfiguration.getWidth());
    backgroundImageView.fitWidthProperty().bind(root.widthProperty());
    backgroundImageView.fitHeightProperty().bind(root.heightProperty());
    // Set the style of the buttons
    applyButtonStyle(singlePlayerButton);
    applyButtonStyle(multiPlayerButton);
    applyButtonStyle(optionsButton);
    applyButtonStyle(quitButton);
    sceneController = SceneConfiguration.getSceneController();

    translateAnimation.setNode(trigger);
    fillAnimation.setShape(background);
    background.setOnMouseClicked(event -> toggleButtonClicked());
    trigger.setOnMouseClicked(e -> toggleButtonClicked());
    switchedOn.addListener((obs, oldState, newState) -> {
      boolean isOn = newState.booleanValue();
      if (isOn) {
        NetworkConfiguration.startGameServer();
      } else {
        NetworkConfiguration.stopGameServer();
      }
      translateAnimation.setToX(isOn ? 100 - 55 : 0);
      fillAnimation.setFromValue(isOn ? Color.WHITE : Color.LIGHTGREEN);
      fillAnimation.setToValue(isOn ? Color.LIGHTGREEN : Color.WHITE);
      animation.play();
    });
  }

  private void toggleButtonClicked() {
    boolean isOn = switchedOn.get();
    switchedOn.set(!isOn);
    translateAnimation.setToX(isOn ? 0 : 100 - 55);
    fillAnimation.setFromValue(isOn ? Color.LIGHTGREEN : Color.WHITE);
    fillAnimation.setToValue(isOn ? Color.WHITE : Color.LIGHTGREEN);
    animation.play();
  }


  // Define the event handler for the single player button
  @FXML
  private void handleSinglePlayer() {
    SinglePlayerSettingsScene scene = (SinglePlayerSettingsScene) SceneConfiguration.getSceneController()
        .getSceneBySceneName(SceneName.SINGLE_PLAYER_SETTINGS);
    if (scene == null) {
      scene = new SinglePlayerSettingsScene();
      SinglePlayerSettingsSceneController singlePlayerSettingsSceneController = new SinglePlayerSettingsSceneController(
          scene);
      scene.setController(singlePlayerSettingsSceneController);
      sceneController.addScene(SceneName.SINGLE_PLAYER_SETTINGS, scene);
    }
    pauseTitleSound();
    sceneController.activate(SceneName.SINGLE_PLAYER_SETTINGS);
  }


  // Define the event handler for the multi player button
  @FXML
  private void handleMultiPlayer() {
    //TODO: Implement MultiPlayer

    JoinOnlineScene scene = (JoinOnlineScene) SceneConfiguration.getSceneController()
        .getSceneBySceneName(SceneName.JOIN_ONLINE);
    if (scene == null) {
      scene = new JoinOnlineScene();
      JoinOnlineSceneController joinOnlineSceneController = new JoinOnlineSceneController(scene);
      scene.setController(joinOnlineSceneController);
      sceneController.addScene(SceneName.JOIN_ONLINE, scene);
    }
    pauseTitleSound();
    sceneController.activate(SceneName.JOIN_ONLINE);

  }

  // Define the event handler for the options button
  @FXML
  private void handleOptions() {
    UserOptionsScene scene = (UserOptionsScene) SceneConfiguration.getSceneController()
        .getSceneBySceneName(SceneName.USER_OPTION);
    if (scene == null) {
      scene = new UserOptionsScene();
      UserOptionsSceneController userOptionsSceneController = new UserOptionsSceneController(scene);
      scene.setController(userOptionsSceneController);
      sceneController.addScene(SceneName.USER_OPTION, scene);
    }
    pauseTitleSound();
    sceneController.activate(SceneName.USER_OPTION);
  }

  @FXML
  private void handleQuitGame() {
    SceneController sceneController = SceneConfiguration.getSceneController();
    sceneController.close();
  }

  private void animateTitleLabel() {
    // Rotate animation
    TranslateTransition movementTransition = new TranslateTransition(Duration.seconds(1),
        titleLabel);
    movementTransition.setByY(-10);
    movementTransition.setCycleCount(Animation.INDEFINITE);
    movementTransition.setAutoReverse(true);
    // Scale animation
    ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(1), titleLabel);
    scaleTransition.setByX(0.1);
    scaleTransition.setByY(0.1);
    scaleTransition.setAutoReverse(true);
    scaleTransition.setCycleCount(Animation.INDEFINITE);

    // Color animation
    ObjectProperty<Color> colorProperty = new SimpleObjectProperty<>(Color.WHITE);
    colorProperty.addListener((observable, oldValue, newValue) -> titleLabel.setTextFill(newValue));

    KeyValue keyValue1 = new KeyValue(colorProperty, Color.web("#0093ff"));
    KeyFrame keyFrame1 = new KeyFrame(Duration.seconds(1), keyValue1);

    KeyValue keyValue2 = new KeyValue(colorProperty, Color.WHITE);
    KeyFrame keyFrame2 = new KeyFrame(Duration.seconds(2), keyValue2);

    Timeline colorTimeline = new Timeline(keyFrame1, keyFrame2);
    colorTimeline.setCycleCount(Animation.INDEFINITE);
    colorTimeline.setAutoReverse(true);

    // Play all animations together
    ParallelTransition parallelTransition = new ParallelTransition(movementTransition,
        scaleTransition, colorTimeline);
    parallelTransition.play();
  }
}
