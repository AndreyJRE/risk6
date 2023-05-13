package com.unima.risk6.gui.uimodels;

import com.unima.risk6.game.configurations.GameConfiguration;
import com.unima.risk6.game.models.Statistic;
import com.unima.risk6.gui.configurations.SceneConfiguration;
import com.unima.risk6.gui.configurations.SoundConfiguration;
import com.unima.risk6.gui.configurations.StyleConfiguration;
import com.unima.risk6.gui.controllers.GameSceneController;
import com.unima.risk6.gui.scenes.GameScene;
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

public class SettingsUi extends BorderPane {

  private final Button continueGameButton;
  private final Button leaveGameButton;
  private final Popup settingPopup;

  private final BorderPane gameRoot;

  public Slider volumeSlider;

  public SettingsUi(GameScene gameScene) {
    super();
    this.gameRoot = (BorderPane) gameScene.getRoot();
    settingPopup = new Popup();
    VBox vBox = new VBox();
    continueGameButton = new Button("Continue Game");
    leaveGameButton = new Button("Leave Game");
    this.volumeSlider = new Slider();
    StyleConfiguration.applyButtonStyle(continueGameButton);
    StyleConfiguration.applyButtonStyle(leaveGameButton);
    continueGameButton.setOnAction(event -> handleContinueButton());
    leaveGameButton.setOnAction(event -> handleLeaveButton());

    vBox.setAlignment(Pos.CENTER);
    vBox.setSpacing(20);
    vBox.getChildren().addAll(volumeSlider, continueGameButton, leaveGameButton);
    this.setCenter(vBox);
    this.setStyle("-fx-background-color: #F5F5F5; -fx-background-radius: 10;");
    continueGameButton.setPrefHeight(50);
    leaveGameButton.setPrefHeight(50);
    Font.loadFont(getClass().getResourceAsStream("/com/unima/risk6/fonts/segoe_ui_bold.ttf"), 26);
    continueGameButton.setFont(new Font(18));
    leaveGameButton.setFont(new Font(18));
    // Slider Styling
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

  public void handleContinueButton() {
    settingPopup.hide();
  }

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
        Statistic statistic = GameSceneController.getPlayerController().getPlayer().getStatistic();
        GameConfiguration.updateGameStatistic(false, statistic.getTroopsLost(),
            statistic.getCountriesWon(), statistic.getTroopsGained(),
            statistic.getCountriesLost());
        SceneConfiguration.getSceneController().close();
      } else {
        alert.close();
        settingPopup.hide();
      }
    });
  }
}

