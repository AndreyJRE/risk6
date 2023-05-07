package com.unima.risk6.gui.uiModels;

import com.unima.risk6.game.logic.EndPhase;
import com.unima.risk6.game.models.enums.GamePhase;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
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

  private Label phaseLabel;

  private Rectangle reinforcementRectangle;

  private Rectangle attackRectangle;

  private Rectangle fortifyRectangle;

  public ActivePlayerUi(double radiusX, double radiusY, double rectangleWidth,
      double rectangleHeight, PlayerUi playerUi) {
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

    if (this.playerUi.getPlayer().getCurrentPhase() == GamePhase.CLAIM_PHASE) {
      phaseLabel = new Label("Claim a Territory!");
      phaseLabel.setStyle(
          "-fx-font-size: 18px; -fx-background-color: white; -fx-font-weight: bold;");
      iconsPane.getChildren().add(phaseLabel);
      iconsPane.setAlignment(phaseLabel, Pos.CENTER);
      getChildren().addAll(rectangle, ellipse, iconsPane);
    } else {
      Image reinforcementImage = new Image(
          getClass().getResource("/com/unima/risk6/pictures/reinforcement.png").toString());
      ImagePattern reinforcementImagePattern = new ImagePattern(reinforcementImage);

      Image attackImage = new Image(
          getClass().getResource("/com/unima/risk6/pictures/sword.png").toString());
      ImagePattern attackImagePattern = new ImagePattern(attackImage);

      Image fortifyImage = new Image(
          getClass().getResource("/com/unima/risk6/pictures/fortify.png").toString());
      ImagePattern fortifyImagePattern = new ImagePattern(fortifyImage);

      reinforcementRectangle = new Rectangle(radiusX, radiusY);
      reinforcementRectangle.setFill(reinforcementImagePattern);
      attackRectangle = new Rectangle(radiusX, radiusY);
      attackRectangle.setFill(attackImagePattern);
      fortifyRectangle = new Rectangle(radiusX, radiusY);
      fortifyRectangle.setFill(fortifyImagePattern);
      System.out.println(this.playerUi.getPlayer().getCurrentPhase());
      switch (this.playerUi.getPlayer().getCurrentPhase()) {
        case REINFORCEMENT_PHASE -> {
          phaseLabel = new Label("Reinforcement");
          attackRectangle.setOpacity(0.5);
          fortifyRectangle.setOpacity(0.5);
        }
        case ATTACK_PHASE -> {
          phaseLabel = new Label("Attack");
          reinforcementRectangle.setOpacity(0.5);
          fortifyRectangle.setOpacity(0.5);
        }
        case FORTIFY_PHASE -> {
          phaseLabel = new Label("Fortify");
          attackRectangle.setOpacity(0.5);
          reinforcementRectangle.setOpacity(0.5);
        }
        case ORDER_PHASE -> {
          phaseLabel = new Label("Order");
          attackRectangle.setOpacity(0.5);
          reinforcementRectangle.setOpacity(0.5);
          fortifyRectangle.setOpacity(0.5);
        }
      }

      VBox iconsAndNameBox = new VBox();
      iconsAndNameBox.setAlignment(Pos.CENTER);
      phaseLabel.setStyle(
          "-fx-font-size: 18px; -fx-background-color: white; -fx-font-weight: " + "bold;");

      HBox iconsHBox = new HBox();
      iconsHBox.getChildren().addAll(reinforcementRectangle, attackRectangle, fortifyRectangle);
      iconsHBox.setSpacing(25);
      iconsHBox.setAlignment(Pos.CENTER);
      iconsAndNameBox.getChildren().addAll(iconsHBox, phaseLabel);

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

  public void changePhase(EndPhase endPhase) {
    switch (endPhase.getPhaseToEnd()) {
      case REINFORCEMENT_PHASE -> {
        phaseLabel.setText("Attack");
        attackRectangle.setOpacity(1);
        reinforcementRectangle.setOpacity(0.5);
        fortifyRectangle.setOpacity(0.5);
      }
      case ATTACK_PHASE -> {
        phaseLabel.setText("Fortify");
        fortifyRectangle.setOpacity(1);
        attackRectangle.setOpacity(0.5);
        reinforcementRectangle.setOpacity(0.5);
      }
      case FORTIFY_PHASE -> {
       /* phaseLabel.setText("Reinforcement");
        reinforcementRectangle.setOpacity(1);
        attackRectangle.setOpacity(0.5);
        fortifyRectangle.setOpacity(0.5);
        */

      }
    }
  }
}
