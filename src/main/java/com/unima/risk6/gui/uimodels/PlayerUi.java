package com.unima.risk6.gui.uimodels;

import com.unima.risk6.game.ai.bots.EasyBot;
import com.unima.risk6.game.ai.bots.HardBot;
import com.unima.risk6.game.ai.bots.MediumBot;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.gui.configurations.ImageConfiguration;
import com.unima.risk6.gui.controllers.enums.Colors;
import com.unima.risk6.gui.controllers.enums.ImageName;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;

/**
 * Represents a graphical user interface (UI) representation of a player.
 *
 * @author mmeider
 */

public class PlayerUi extends Group {

  private Player player;

  private final Color playerColor;

  private Ellipse ellipse;

  private Label amountOfTroops;

  private Rectangle rectangle;

  /**
   * Creates the UI representation of a player with the specified parameters.
   *
   * @param player          The player object.
   * @param playerColor     The color of the player.
   * @param radiusX         The X radius of the ellipse.
   * @param radiusY         The Y radius of the ellipse.
   * @param rectangleWidth  The width of the rectangle.
   * @param rectangleHeight The height of the rectangle.
   */

  public PlayerUi(Player player, Color playerColor,
      double radiusX, double radiusY,
      double rectangleWidth, double rectangleHeight) {
    this.player = player;
    this.playerColor = playerColor;
    ellipse = new Ellipse(0, 0, radiusX, radiusY);
    ImageView userImage;
    if (player instanceof EasyBot) {
      userImage = new ImageView(ImageConfiguration.getImageByName(ImageName.EASYBOT_ICON));
    } else if (player instanceof MediumBot) {
      userImage = new ImageView(ImageConfiguration.getImageByName(ImageName.MEDIUMBOT_ICON));
    } else if (player instanceof HardBot) {
      userImage = new ImageView(ImageConfiguration.getImageByName(ImageName.HARDBOT_ICON));
    } else {
      userImage = new ImageView(ImageConfiguration.getImageByName(ImageName.PLAYER_ICON));
    }
    StackPane stackPane = new StackPane(userImage);
    stackPane.setStyle("-fx-background-color: #F5F5F5;");
    //stackPane.setBackground(new Background(new BackgroundFill(Colors.COUNTRY_BACKGROUND.getColor(), CornerRadii.EMPTY, Insets.EMPTY)));

    ellipse.setFill(new ImagePattern(stackPane.snapshot(null, null)));
    ellipse.setStroke(playerColor);
    ellipse.setStrokeWidth(3);
    rectangle = new Rectangle(rectangleWidth, rectangleHeight);
    rectangle.setFill(Colors.COUNTRY_BACKGROUND.getColor());
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
    StackPane playerNameStack = new StackPane();
    Label userLabel = new Label(player.getUser());
    //userLabel.setBackground(new Background(new BackgroundFill(Colors.COUNTRY_BACKGROUND.getColor(), CornerRadii.EMPTY, Insets.EMPTY)));
    Rectangle userRectangle;
    if (userLabel.getWidth() > ellipse.getRadiusX() * 2 + 15) {
      userRectangle = new Rectangle(userLabel.getWidth(), userLabel.getHeight() + 20);
    } else {
      userRectangle = new Rectangle(ellipse.getRadiusX() * 2 + 15, userLabel.getHeight() + 20);
    }
    userRectangle.setFill(Colors.COUNTRY_BACKGROUND.getColor());
    userRectangle.setStroke(this.getPlayerColor());
    userRectangle.setStrokeWidth(2);
    userRectangle.setArcWidth(ellipse.getRadiusX() - 10);
    userRectangle.setArcHeight(userRectangle.getHeight());
    userLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");
    playerNameStack.setPrefSize(userRectangle.getWidth(), userRectangle.getHeight());
    playerNameStack.setLayoutX(0 - ellipse.getRadiusX() - 10);
    playerNameStack.setLayoutY(ellipse.getRadiusY() - 10);
    playerNameStack.getChildren().addAll(userRectangle, userLabel);
    //playerNameStack.setBackground(new Background(new BackgroundFill(Colors.COUNTRY_BACKGROUND.getColor(), CornerRadii., Insets.EMPTY)));
    StackPane.setAlignment(userLabel, Pos.CENTER);
    getChildren().addAll(rectangle, ellipse, iconsPane, playerNameStack);
    /*
    DropShadow dropShadow = new DropShadow();
    dropShadow.setColor(Color.BLACK);
    dropShadow.setSpread(0.5);
    dropShadow.setBlurType(BlurType.ONE_PASS_BOX);
    this.setEffect(dropShadow);

     */

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
