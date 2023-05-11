package com.unima.risk6.gui.uiModels;

import com.unima.risk6.game.ai.bots.EasyBot;
import com.unima.risk6.game.ai.bots.HardBot;
import com.unima.risk6.game.ai.bots.MediumBot;
import com.unima.risk6.game.models.Player;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;

public class PlayerUi extends Group {

  private Player player;

  private final Color playerColor;

  private Ellipse ellipse;

  private Label amountOfTroops;

  private Rectangle rectangle;

  public PlayerUi(Player player, Color playerColor,
      double radiusX, double radiusY,
      double rectangleWidth, double rectangleHeight) {
    this.player = player;
    this.playerColor = playerColor;
    ellipse = new Ellipse(0, 0, radiusX, radiusY);
    ImageView userImage;
    if (player instanceof EasyBot) {
      userImage = new ImageView(new Image(
          getClass().getResource("/com/unima/risk6/pictures/easyBot.png").toString()));
    } else if (player instanceof MediumBot) {
      userImage = new ImageView(new Image(
          getClass().getResource("/com/unima/risk6/pictures/mediumBot.png").toString()));
    } else if (player instanceof HardBot) {
      userImage = new ImageView(new Image(
          getClass().getResource("/com/unima/risk6/pictures/hardBot.png").toString()));
    } else {
      userImage = new ImageView(new Image(
          getClass().getResource("/com/unima/risk6/pictures/playerIcon.png").toString()));
    }

    StackPane stackPane = new StackPane(userImage);
    stackPane.setStyle("-fx-background-color: #F5F5F5;");

    ellipse.setFill(new ImagePattern(stackPane.snapshot(null, null)));
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
    iconsPane.setPrefSize(rectangleWidth, rectangleHeight - 10);
    iconsPane.setAlignment(Pos.CENTER);
    iconsPane.setLayoutX(30);
    iconsPane.setLayoutY(5 - rectangleHeight / 2);

    Image soldierImage = new Image(
        getClass().getResource("/com/unima/risk6/pictures/soldier.png").toString());
    ImagePattern soldierImagePattern = new ImagePattern(soldierImage);

    Rectangle icon1 = new Rectangle(radiusX, radiusY);
    icon1.setFill(soldierImagePattern);

    amountOfTroops = new Label();
    amountOfTroops.setStyle("-fx-font-size: 16px; -fx-font-weight: bold");

    StackPane.setMargin(amountOfTroops, new Insets(0, 58, 0, 0));

    updateAmountOfTroops();

    iconsPane.getChildren().addAll(amountOfTroops, icon1);
    StackPane.setAlignment(icon1, Pos.CENTER);

    getChildren().addAll(rectangle, ellipse, iconsPane);

    DropShadow dropShadow = new DropShadow();
    dropShadow.setColor(Color.BLACK);
    dropShadow.setSpread(0.5);
    dropShadow.setBlurType(BlurType.ONE_PASS_BOX);

    this.setEffect(dropShadow);

  }

  public void updateAmountOfTroops() {
    this.amountOfTroops.setText(Integer.toString(player.getStatistic().getNumberOfTroops()));
  }

  public void setPlayer(Player player) {
    this.player = player;
  }

  public Player getPlayer() {
    return player;
  }

  public Color getPlayerColor() {
    return playerColor;
  }
}
