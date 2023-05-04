package com.unima.risk6.gui.uiModels;

import com.unima.risk6.game.models.Player;
import com.unima.risk6.game.models.enums.GamePhase;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;

public class ActivePlayerUi extends Group {

  private Ellipse ellipse;

  private Rectangle rectangle;

  private PlayerUi playerUi;

  public ActivePlayerUi(double radiusX, double radiusY,
      double rectangleWidth, double rectangleHeight, PlayerUi playerUi, GamePhase mockGamePhase) {
    this.setId("activePlayerUi");
    this.playerUi = playerUi;
    ellipse = new Ellipse(0, 0, radiusX, radiusY);
    ellipse.setFill(Color.WHITE);
    ellipse.setStroke(this.playerUi.getPlayerColor());
    ellipse.setStrokeWidth(3);
    rectangle = new Rectangle(rectangleWidth, rectangleHeight);
    rectangle.setFill(Color.WHITE);
    rectangle.setStroke(this.playerUi.getPlayerColor());
    rectangle.setStrokeWidth(2);
    rectangle.setArcWidth(rectangleHeight);
    rectangle.setArcHeight(rectangleHeight);
    rectangle.setLayoutX(0);
    rectangle.setLayoutY(0 - rectangleHeight / 2);

    StackPane iconsPane = new StackPane();
    iconsPane.setPrefSize(rectangleWidth - 80, rectangleHeight - 10);
    iconsPane.setAlignment(Pos.CENTER);
    iconsPane.setLayoutX(50);
    iconsPane.setLayoutY(5 - rectangleHeight / 2);

    if (mockGamePhase == GamePhase.CLAIM_PHASE) {
      Label claimLabel = new Label("Claim a Territory!");
      claimLabel.setStyle("-fx-font-size: 18px; -fx-background-color: white;");
      iconsPane.getChildren().add(claimLabel);
      iconsPane.setAlignment(claimLabel, Pos.CENTER);
      getChildren().addAll(rectangle, ellipse, iconsPane);
    } else {
      String phaseString = "";
      Image reinforcementImage = new Image(
          getClass().getResource("/com/unima/risk6/pictures/reinforcement.png").toString());
      ImagePattern reinforcementImagePattern = new ImagePattern(reinforcementImage);

      Image attackImage = new Image(
          getClass().getResource("/com/unima/risk6/pictures/sword.png").toString());
      ImagePattern attackImagePattern = new ImagePattern(attackImage);

      Image fortifyImage = new Image(
          getClass().getResource("/com/unima/risk6/pictures/fortify.png").toString());
      ImagePattern fortifyImagePattern = new ImagePattern(fortifyImage);

      Rectangle reinforcementRectangle = new Rectangle(radiusX, radiusY);
      reinforcementRectangle.setFill(reinforcementImagePattern);
      Rectangle attackRectangle = new Rectangle(radiusX, radiusY);
      attackRectangle.setFill(attackImagePattern);
      Rectangle fortifyRectangle = new Rectangle(radiusX, radiusY);
      fortifyRectangle.setFill(fortifyImagePattern);

      switch (mockGamePhase) {
        case REINFORCEMENT_PHASE:
          phaseString = "Reinforcement";
          attackRectangle.setOpacity(0.5);
          fortifyRectangle.setOpacity(0.5);
          break;
        case ATTACK_PHASE:
          phaseString = "Attack";
          reinforcementRectangle.setOpacity(0.5);
          fortifyRectangle.setOpacity(0.5);
          break;
        case FORTIFY_PHASE:
          phaseString = "Fortify";
          attackRectangle.setOpacity(0.5);
          reinforcementRectangle.setOpacity(0.5);
          break;
      }

      VBox iconsAndNameBox = new VBox();
      iconsAndNameBox.setAlignment(Pos.CENTER);
      Label claimLabel = new Label(phaseString);
      claimLabel.setStyle("-fx-font-size: 18px; -fx-background-color: white;");

      HBox iconsHBox = new HBox();
      iconsHBox.getChildren().addAll(reinforcementRectangle, attackRectangle, fortifyRectangle);
      iconsHBox.setSpacing(25);
      iconsHBox.setAlignment(Pos.CENTER);
      iconsAndNameBox.getChildren().addAll(iconsHBox, claimLabel);

      iconsPane.getChildren().add(iconsAndNameBox);
      getChildren().addAll(rectangle, ellipse, iconsPane);
    }
  }

  public PlayerUi getPlayerUi() {
    return playerUi;
  }

  public void changeActivePlayerUi(PlayerUi playerUi) {
    this.playerUi = playerUi;
    ellipse.setStroke(playerUi.getPlayerColor());
    rectangle.setStroke(playerUi.getPlayerColor());
  }
}
