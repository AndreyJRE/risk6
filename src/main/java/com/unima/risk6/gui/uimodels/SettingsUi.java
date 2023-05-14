package com.unima.risk6.gui.uimodels;

import com.unima.risk6.game.configurations.GameConfiguration;
import com.unima.risk6.game.configurations.LobbyConfiguration;
import com.unima.risk6.game.models.Statistic;
import com.unima.risk6.gui.configurations.SceneConfiguration;
import com.unima.risk6.gui.configurations.SoundConfiguration;
import com.unima.risk6.gui.configurations.StyleConfiguration;
import com.unima.risk6.gui.controllers.GameSceneController;
import com.unima.risk6.gui.controllers.SceneController;
import com.unima.risk6.gui.controllers.enums.SceneName;
import com.unima.risk6.gui.scenes.GameScene;
import com.unima.risk6.network.configurations.NetworkConfiguration;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Popup;

/**
 * Represents a SettingsUi object, which extends BorderPane and provides options for adjusting game
 * settings such as volume and leaving the game.
 *
 * @author astoyano
 */

public class SettingsUi extends BorderPane {

  private final Button continueGameButton;
  private final Button leaveGameButton;
  private final Popup settingPopup;

  private final BorderPane gameRoot;

  public Slider volumeSlider;

  /**
   * Constructs a SettingsUi object with the specified GameScene.
   *
   * @param gameScene The GameScene object representing the game.
   */
  public SettingsUi(GameScene gameScene) {
    super();
    this.gameRoot = (BorderPane) gameScene.getRoot();
    settingPopup = new Popup();
    continueGameButton = new Button("Continue Game");
    leaveGameButton = new Button("Leave Game");
    this.volumeSlider = new Slider();
    StyleConfiguration.applyButtonStyle(continueGameButton);
    StyleConfiguration.applyButtonStyle(leaveGameButton);
    continueGameButton.setOnAction(event -> handleContinueButton());
    leaveGameButton.setOnAction(event -> handleLeaveButton());
    VBox optionsBox = new VBox();
    optionsBox.setAlignment(Pos.CENTER);
    optionsBox.setSpacing(20);
    optionsBox.getChildren().addAll(volumeSlider, continueGameButton, leaveGameButton);
    this.setCenter(optionsBox);
    this.setStyle("-fx-background-color: #F5F5F5; -fx-background-radius: 10;");
    continueGameButton.setPrefHeight(50);
    leaveGameButton.setPrefHeight(50);
    Font.loadFont(getClass().getResourceAsStream("/com/unima/risk6/fonts/segoe_ui_bold.ttf"), 26);
    continueGameButton.setFont(new Font(18));
    leaveGameButton.setFont(new Font(18));
    volumeSlider.setStyle("-fx-control-inner-background: #F5F5F5; -fx-accent: #007BFF;");
    volumeSlider.setMinWidth(100);
    volumeSlider.setStyle("-fx-control-inner-background: #F5F5F5; -fx-accent: #007BFF;");
    volumeSlider.setValue(SoundConfiguration.getVolume() * 100.0);
    volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
      SoundConfiguration.setVolume(newValue.doubleValue() / 100.0);
    });
    settingPopup.setAutoHide(true);
    settingPopup.setAutoFix(true);
    settingPopup.getContent().add(this);
  }

  /**
   * Displays the SettingsUi by setting its size, positioning it in the center of the game scene,
   * and showing it as a popup.
   */
  public void show() {
    this.setPrefSize(gameRoot.getWidth() * 0.25, gameRoot.getHeight() * 0.25);
    continueGameButton.setMinWidth(this.getPrefWidth() - 20);
    leaveGameButton.setMinWidth(this.getPrefWidth() - 20);
    volumeSlider.setPrefWidth(this.getPrefWidth() - 20);
    Bounds rootBounds = gameRoot.localToScreen(gameRoot.getBoundsInLocal());
    double centerX = rootBounds.getMinX() + rootBounds.getWidth() / 2;
    double centerY = rootBounds.getMinY() + rootBounds.getHeight() / 2;
    double popupWidth = this.getPrefWidth();
    double popupHeight = this.getPrefHeight();
    settingPopup.setX(centerX - popupWidth / 2);
    settingPopup.setY(centerY - popupHeight / 2);
    settingPopup.show(gameRoot.getScene().getWindow());
  }

  /**
   * Handles the action when the "Leave Game" button is clicked. Displays a confirmation dialog and
   * takes appropriate action based on the user's choice.
   */
  public void handleLeaveButton() {
    Alert alert = new Alert(AlertType.WARNING);
    alert.setTitle("Warning: Exiting Game");
    alert.setHeaderText("Are you sure you want to to leave the game?");
    alert.setContentText(
        "If you leave, you cannot rejoin the game! Your place will be replaced " + "by a bot.");
    ButtonType buttonYes = new ButtonType("Yes, exit game");
    ButtonType buttonNo = new ButtonType("No, continue playing");
    alert.getButtonTypes().setAll(buttonYes, buttonNo);
    alert.showAndWait().ifPresent(buttonType -> {
      if (buttonType == buttonYes) {
        if (GameConfiguration.getCurrentGameStatistic() != null) {
          Statistic statistic = GameSceneController.getPlayerController().getPlayer()
              .getStatistic();
          GameConfiguration.updateGameStatistic(false, statistic.getTroopsLost(),
              statistic.getCountriesWon(), statistic.getTroopsGained(),
              statistic.getCountriesLost());
        }
        try {
          SoundConfiguration.stopInGameMusic();
          LobbyConfiguration.stopGameClient();
          Thread.sleep(250);
          if (NetworkConfiguration.getGameServer().getHostIp().equals("127.0.0.1")) {
            NetworkConfiguration.stopGameServer();
          }
          Thread.sleep(150);
          SceneController sceneController = SceneConfiguration.getSceneController();
          sceneController.activate(SceneName.TITLE);
          sceneController.getStage().setMaximized(false);
          sceneController.getStage().setWidth(1220);
          sceneController.getStage().setHeight(820);
          SceneConfiguration.setWidth(1220);
          SceneConfiguration.setHeight(820);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }

      } else {
        alert.close();
        settingPopup.hide();
      }
    });
  }

  /**
   * Handles the action when the "Continue Game" button is clicked. Hides the SettingsUi.
   */
  public void handleContinueButton() {
    settingPopup.hide();
  }
}

