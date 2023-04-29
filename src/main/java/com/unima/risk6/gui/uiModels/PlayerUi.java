package com.unima.risk6.gui.uiModels;

import com.unima.risk6.game.models.Player;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;

public class PlayerUi extends Group {

  private Player player;

  private Color playerColor;

  private Ellipse ellipse;

  private Rectangle rectangle;

  public PlayerUi(Player player, Color playerColor,
      double radiusX, double radiusY,
      double rectangleWidth, double rectangleHeight) {
    this.player = player;
    this.playerColor = playerColor;
    ellipse = new Ellipse(0, 0, radiusX, radiusY);
    ellipse.setFill(Color.WHITE);
    ellipse.setStroke(playerColor);
    ellipse.setStrokeWidth(3);

    rectangle = new Rectangle(rectangleWidth, rectangleHeight);
    rectangle.setFill(Color.WHITE);
    rectangle.setStroke(playerColor);
    rectangle.setStrokeWidth(3);
    rectangle.setArcWidth(rectangleHeight);
    rectangle.setArcHeight(rectangleHeight);
    rectangle.setLayoutX(0);
    rectangle.setLayoutY(0 - rectangleHeight / 2);

    StackPane iconsPane = new StackPane();
    iconsPane.setPrefSize(rectangleWidth - 60, rectangleHeight - 10);
    iconsPane.setAlignment(Pos.CENTER);
    iconsPane.setLayoutX(50);
    iconsPane.setLayoutY(5 - rectangleHeight / 2);

    Image soldierImage = new Image(
        getClass().getResource("/com/unima/risk6/pictures/soldier.png").toString());
    ImagePattern soldierImagePattern = new ImagePattern(soldierImage);

    Rectangle icon1 = new Rectangle(radiusX, radiusY);
    icon1.setFill(soldierImagePattern);

    iconsPane.getChildren().addAll(icon1);
    StackPane.setAlignment(icon1, Pos.CENTER);

    getChildren().addAll(rectangle, ellipse, iconsPane);

    DropShadow dropShadow = new DropShadow();
    dropShadow.setColor(Color.BLACK);
    dropShadow.setSpread(0.5);
    dropShadow.setBlurType(BlurType.ONE_PASS_BOX);

    this.setEffect(dropShadow);

  }

  public Player getPlayer() {
    return player;
  }

  public Color getPlayerColor() {
    return playerColor;
  }
}
