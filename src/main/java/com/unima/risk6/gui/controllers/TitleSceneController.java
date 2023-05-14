package com.unima.risk6.gui.controllers;

import static com.unima.risk6.gui.configurations.SoundConfiguration.pauseTitleSound;
import static com.unima.risk6.gui.configurations.StyleConfiguration.applyButtonStyle;
import static com.unima.risk6.gui.configurations.StyleConfiguration.showErrorDialog;

import com.unima.risk6.game.configurations.GameConfiguration;
import com.unima.risk6.game.configurations.LobbyConfiguration;
import com.unima.risk6.game.models.GameLobby;
import com.unima.risk6.game.models.UserDto;
import com.unima.risk6.gui.configurations.ImageConfiguration;
import com.unima.risk6.gui.configurations.SceneConfiguration;
import com.unima.risk6.gui.configurations.SessionManager;
import com.unima.risk6.gui.configurations.SoundConfiguration;
import com.unima.risk6.gui.controllers.enums.ImageName;
import com.unima.risk6.gui.controllers.enums.SceneName;
import com.unima.risk6.gui.scenes.JoinOnlineScene;
import com.unima.risk6.gui.scenes.UserOptionsScene;
import com.unima.risk6.network.configurations.NetworkConfiguration;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
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
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;

/**
 * The class TitleSceneController controls the title scene of the game. It includes methods for
 * button actions like single player, multiplayer, tutorial, options and quit. It also includes
 * methods for animations, handling sound volume and IP address display.
 *
 * @author fisommer
 * @author astoyano
 * @author eameri
 */

public class TitleSceneController implements Initializable {

  @FXML
  public Slider volumeSlider;
  @FXML
  ImageView volumeImage;
  @FXML
  private AnchorPane root;
  @FXML
  private MediaView backgroundVideoView;
  @FXML
  private Label titleLabel;
  @FXML
  private Button singlePlayerButton;
  @FXML
  private Button multiPlayerButton;
  @FXML
  private Button tutorialButton;
  @FXML
  private Button optionsButton;
  @FXML
  private Button quitButton;
  @FXML
  private Rectangle background;
  @FXML
  private Circle trigger;
  @FXML
  private TextField ipLabel;
  private BooleanProperty switchedOn;
  private TranslateTransition translateAnimation;
  private FillTransition fillAnimation;
  private ParallelTransition animation;
  private GameLobby gameLobby;


  private SceneController sceneController;
  private double volume;

  /**
   * Gets the IP addresses of the machine.
   *
   * @return A StringBuilder containing the IP addresses.
   */

  private static StringBuilder getIpS() {
    StringBuilder ipS = new StringBuilder();
    try {
      Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
      for (NetworkInterface netint : Collections.list(nets)) {
        Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
        for (InetAddress inetAddress : Collections.list(inetAddresses)) {
          String ip = inetAddress.getHostAddress();
          if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()
              && inetAddress.getAddress().length == 4) {
            ipS.append(ip);
            ipS.append(",");
          }
        }
      }
    } catch (SocketException e) {
      throw new RuntimeException(e);
    }
    return ipS;
  }

  /**
   * Initialize the controller. It sets the initial configurations and properties of the elements in
   * the title scene such as the volume, video, animations, and IP address display.
   *
   * @param url            Represents a Uniform Resource Locator, a pointer to a "resource" on the
   *                       World Wide Web.
   * @param resourceBundle Contains locale-specific objects.
   */

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    applyButtonStyle(singlePlayerButton);
    applyButtonStyle(multiPlayerButton);
    applyButtonStyle(tutorialButton);
    applyButtonStyle(optionsButton);
    applyButtonStyle(quitButton);
    volumeSlider.setValue(SoundConfiguration.getVolume() * 100);
    volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
      SoundConfiguration.setVolume(newValue.doubleValue() / 100.0);
    });
    volumeImage.hoverProperty().addListener((observable, oldVal, nval) -> {
      if (nval) {
        volumeImage.setCursor(Cursor.HAND);
      } else {
        volumeImage.setCursor(Cursor.DEFAULT);
      }
    });
    MediaPlayer mediaPlayer = new MediaPlayer(ImageConfiguration.getTitleBackgroundVideo());
    backgroundVideoView.setMediaPlayer(mediaPlayer);
    mediaPlayer.setOnEndOfMedia(() -> {
      // Das Video von vorne beginnen
      mediaPlayer.seek(Duration.ZERO);
    });
    mediaPlayer.play();
    backgroundVideoView.fitWidthProperty().bind(root.widthProperty());
    backgroundVideoView.fitHeightProperty().bind(root.heightProperty());
    switchedOn = new SimpleBooleanProperty(false);

    translateAnimation = new TranslateTransition(Duration.seconds(0.25));
    fillAnimation = new FillTransition(Duration.seconds(0.25));
    animation = new ParallelTransition(translateAnimation, fillAnimation);
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
    switchedOn.setValue(NetworkConfiguration.getGameServerThread() != null
        && NetworkConfiguration.getGameServerThread().isAlive());
    setIpLabel();
    boolean isServerLive = switchedOn.get();
    translateAnimation.setToX(isServerLive ? 45 : 0);
    fillAnimation.setFromValue(isServerLive ? Color.WHITE : Color.LIGHTGREEN);
    fillAnimation.setToValue(isServerLive ? Color.LIGHTGREEN : Color.WHITE);
    animation.play();
    switchedOn.addListener((obs, oldState, newState) -> {
      boolean isOn = newState;
      if (isOn) {
        NetworkConfiguration.startGameServer();
      } else {
        NetworkConfiguration.stopGameServer();
      }
      translateAnimation.setToX(isOn ? 45 : 0);
      fillAnimation.setFromValue(isOn ? Color.WHITE : Color.LIGHTGREEN);
      fillAnimation.setToValue(isOn ? Color.LIGHTGREEN : Color.WHITE);
      animation.play();
    });

    toggleLocalButtons(switchedOn.get());
  }

  /**
   * Handles the volume clicked event. It either mutes or unmutes the volume based on the current
   * volume level.
   */

  @FXML
  private void volumeClicked() {
    if (volumeSlider.getValue() == 0.0) {
      volumeSlider.setValue(volume);
      volumeImage.setImage(ImageConfiguration.getImageByName(ImageName.SOUND_ICON));
    } else {
      volume = volumeSlider.getValue();
      volumeSlider.setValue(0.0);
      volumeImage.setImage(ImageConfiguration.getImageByName(ImageName.MUTED_ICON));
    }

  }

  /**
   * Handles the button click event to toggle the multiplayer and tutorial buttons.
   */

  private void toggleButtonClicked() {
    boolean isOn = switchedOn.get();
    switchedOn.set(!isOn);
    toggleLocalButtons(switchedOn.get());
    setIpLabel();
    translateAnimation.setToX(isOn ? 0 : 100 - 55);
    fillAnimation.setFromValue(isOn ? Color.LIGHTGREEN : Color.WHITE);
    fillAnimation.setToValue(isOn ? Color.WHITE : Color.LIGHTGREEN);
    animation.play();
  }

  private void toggleLocalButtons(boolean isOn) {
    if (!isOn) {
      singlePlayerButton.setOpacity(1);
      singlePlayerButton.setDisable(false);
      tutorialButton.setOpacity(1);
      tutorialButton.setDisable(false);
    } else { // host enabled
      singlePlayerButton.setOpacity(0.6);
      singlePlayerButton.setDisable(true);
      tutorialButton.setOpacity(0.6);
      tutorialButton.setDisable(true);
    }
  }

  /**
   * Sets the IP address label in the scene.
   */

  private void setIpLabel() {
    if (switchedOn.get()) {
      StringBuilder ipS = getIpS();
      ipLabel.setStyle(
          "-fx-background-color: transparent; -fx-font-size: 20px; -fx-text-fill: #ffffff");
      String[] splitIp = ipS.toString().split(",");
      String string = Arrays.toString(splitIp);
      if (splitIp.length > 1) {
        ipLabel.setText("Your IP Addresses: " + string);
      } else {
        ipLabel.setText("Your IP Address: " + string);
      }
      ipLabel.setEditable(false);
      ipLabel.setPrefWidth(500.0);
    } else {
      ipLabel.setStyle(
          "-fx-background-color: transparent; -fx-font-size: 20px; -fx-text-fill: #ffffff");
      ipLabel.setText("");
      ipLabel.setEditable(false);
    }
  }

  /**
   * Handles the single player button click event. It sets up the game lobby for the single player
   * mode and sends a request to the server to join the lobby.
   *
   * @throws InterruptedException if any thread has interrupted the current thread.
   */

  @FXML
  private void handleSinglePlayer() throws InterruptedException {
    if (switchedOn.get()) {
      NetworkConfiguration.stopGameServer();
      Thread.sleep(200);
      switchedOn.setValue(false);
    }
    NetworkConfiguration.startSinglePlayerServer();
    Thread.sleep(100);
    LobbyConfiguration.configureGameClient("127.0.0.1");
    LobbyConfiguration.startGameClient();
    int i = 0;
    while (LobbyConfiguration.getGameClient().getHandler() == null) {
      Thread.sleep(50);
    }
    while (!LobbyConfiguration.getGameClient().isHandshakeComplete()) {
      Thread.sleep(50);
    }
    if (i >= 20) {
      showErrorDialog("Connection error.", "Please start the game again.");
      return;
    }
    Thread.sleep(250);
    GameConfiguration.setMyGameUser(
        new UserDto(SessionManager.getUser().getUsername(), 0, 0, 0, 0, 0));
    LobbyConfiguration.sendJoinServer(GameConfiguration.getMyGameUser());
    Thread.sleep(100);
    gameLobby = new GameLobby("Single Player Lobby", 6, SessionManager.getUser().getUsername(),
        false, 0, GameConfiguration.getMyGameUser());
    gameLobby.getUsers().add(GameConfiguration.getMyGameUser());
    pauseTitleSound();
    SoundConfiguration.playClickSound();
    LobbyConfiguration.sendCreateLobby(gameLobby);
  }

  /**
   * Handles the multiplayer button click event. It switches the scene to the JoinOnlineScene.
   */

  @FXML
  private void handleMultiPlayer() {
    JoinOnlineScene scene = (JoinOnlineScene) SceneConfiguration.getSceneController()
        .getSceneBySceneName(SceneName.JOIN_ONLINE);
    if (scene == null) {
      scene = new JoinOnlineScene();
      JoinOnlineSceneController joinOnlineSceneController = new JoinOnlineSceneController(scene);
      scene.setController(joinOnlineSceneController);
      sceneController.addScene(SceneName.JOIN_ONLINE, scene);
    }
    pauseTitleSound();
    SoundConfiguration.playClickSound();
    sceneController.activate(SceneName.JOIN_ONLINE);

  }

  /**
   * Handles the tutorial button click event. It sets up the game lobby for the tutorial mode and
   * sends a request to the server to join the tutorial lobby.
   *
   * @throws InterruptedException if any thread has interrupted the current thread.
   */

  @FXML
  private void handleTutorial() throws InterruptedException {
    if (switchedOn.get()) {
      NetworkConfiguration.stopGameServer();
      switchedOn.setValue(false);
      Thread.sleep(200);
    }
    NetworkConfiguration.startGameServer();
    Thread.sleep(200);
    LobbyConfiguration.configureGameClient("127.0.0.1");
    LobbyConfiguration.startGameClient();

    int i = 0;
    while (LobbyConfiguration.getGameClient().getHandler() == null) {
      Thread.sleep(50);
    }
    while (!LobbyConfiguration.getGameClient().isHandshakeComplete()) {
      Thread.sleep(50);
    }
    /*while (LobbyConfiguration.getGameClient().getCh() == null && i < 20) {
      Thread.sleep(50);
      i++;
    }*/
    if (i >= 20) {
      showErrorDialog("Connection error.", "Please start the game again.");
      return;
    }
    Thread.sleep(250);
    GameConfiguration.setMyGameUser(
        new UserDto(SessionManager.getUser().getUsername(), 0, 0, 0, 0, 0));
    LobbyConfiguration.sendJoinServer(GameConfiguration.getMyGameUser());
    Thread.sleep(100);
    gameLobby = new GameLobby("Tutorial Lobby", 2, SessionManager.getUser().getUsername(), true, 0,
        GameConfiguration.getMyGameUser());
    gameLobby.getUsers().add(GameConfiguration.getMyGameUser());
    gameLobby.getBots().add("Johnny Test");
    SoundConfiguration.playClickSound();
    pauseTitleSound();
    LobbyConfiguration.sendTutorialCreateLobby(gameLobby);
  }

  /**
   * Handles the options button click event. It switches the scene to the UserOptionsScene.
   */

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
    SoundConfiguration.playClickSound();
    sceneController.activate(SceneName.USER_OPTION);
  }

  /**
   * Handles the quit game button click event. It closes the application.
   */

  @FXML
  private void handleQuitGame() {
    SoundConfiguration.playClickSound();
    SceneController sceneController = SceneConfiguration.getSceneController();
    sceneController.close();
  }

  /**
   * Animates the title label. It creates and plays an animation that includes rotation, scaling and
   * color change.
   */

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
