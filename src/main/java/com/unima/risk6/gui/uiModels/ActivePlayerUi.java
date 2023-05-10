package com.unima.risk6.gui.uiModels;

import com.unima.risk6.game.ai.bots.EasyBot;
import com.unima.risk6.game.ai.bots.HardBot;
import com.unima.risk6.game.ai.bots.MediumBot;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.game.models.enums.GamePhase;
import com.unima.risk6.gui.configurations.SoundConfiguration;
import com.unima.risk6.gui.controllers.GameSceneController;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;

public class ActivePlayerUi extends Group {

  private final StackPane iconsPane;
  private Ellipse ellipse;

  private Rectangle rectangle;

  private PlayerUi playerUi;

  private Label phaseLabel;

  private Rectangle reinforcementRectangle;

  private Rectangle attackRectangle;

  private Rectangle fortifyRectangle;

  private Label userLabel;

  private Rectangle userRectangle;

  public ActivePlayerUi(double radiusX, double radiusY, double rectangleWidth,
      double rectangleHeight, PlayerUi playerUi) {
    this.setId("activePlayerUi");
    this.playerUi = playerUi;
    ellipse = new Ellipse(0, 0, radiusX, radiusY);
    ImageView userImage;
    Player player = playerUi.getPlayer();
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
    iconsPane = new StackPane();
    iconsPane.setPrefSize(rectangleWidth - 80, rectangleHeight - 10);
    iconsPane.setAlignment(Pos.CENTER);
    iconsPane.setLayoutX(50);
    iconsPane.setLayoutY(5 - rectangleHeight / 2);
    phaseLabel = new Label("Test vor Phasen");
    phaseLabel.setStyle("-fx-font-size: 18px; -fx-background-color: white; -fx-font-weight: bold;");
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
    phaseLabel.setText("Claim a territory");
    iconsPane.getChildren().add(phaseLabel);
    StackPane.setAlignment(phaseLabel, Pos.CENTER);

    userLabel = new Label(player.getUser());
    userLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");
    if (userLabel.getWidth() > ellipse.getRadiusX() * 2 + 15) {
      userRectangle = new Rectangle(userLabel.getWidth(), userLabel.getHeight() + 20);
    } else {
      userRectangle = new Rectangle(ellipse.getRadiusX() * 2 + 15, userLabel.getHeight() + 20);
    }
    userRectangle.setFill(Color.WHITE);
    userRectangle.setStroke(this.playerUi.getPlayerColor());
    userRectangle.setStrokeWidth(2);
    userRectangle.setArcWidth(ellipse.getRadiusX() - 10);
    userRectangle.setArcHeight(userRectangle.getHeight());

    StackPane playerNameStack = new StackPane();
    playerNameStack.setPrefSize(userRectangle.getWidth(), userRectangle.getHeight());
    playerNameStack.setLayoutX(0 - ellipse.getRadiusX() - 10);
    playerNameStack.setLayoutY(ellipse.getRadiusY() - 10);
    playerNameStack.getChildren().addAll(userRectangle, userLabel);
    StackPane.setAlignment(userLabel, Pos.CENTER);

    getChildren().addAll(rectangle, ellipse, iconsPane, playerNameStack);

  }

  public PlayerUi getPlayerUi() {
    return playerUi;
  }

  public void changeActivePlayerUi(PlayerUi playerUi) {
    if (!this.playerUi.getPlayer().getUser().equals(playerUi.getPlayer().getUser())
        && this.playerUi.getPlayer().getUser()
        .equals(GameSceneController.getPlayerController().getPlayer().getUser())
        && playerUi.getPlayer().getCurrentPhase() != GamePhase.CLAIM_PHASE) {
      SoundConfiguration.playYourTurnSound();
    }
    this.playerUi = playerUi;
    Player player = playerUi.getPlayer();
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
    ellipse.setStroke(playerUi.getPlayerColor());
    rectangle.setStroke(playerUi.getPlayerColor());
    userRectangle.setStroke(playerUi.getPlayerColor());
    userLabel.setText(player.getUser());
    switch (playerUi.getPlayer().getCurrentPhase()) {
      case REINFORCEMENT_PHASE -> {
        phaseLabel.setText("Reinforcement");
        reinforcementRectangle.setOpacity(1);
        attackRectangle.setOpacity(0.5);
        fortifyRectangle.setOpacity(0.5);
        initPhase();
      }
      case ATTACK_PHASE -> {
        phaseLabel.setText("Attack");
        attackRectangle.setOpacity(1);
        reinforcementRectangle.setOpacity(0.5);
        fortifyRectangle.setOpacity(0.5);
        initPhase();
      }
      case FORTIFY_PHASE -> {
        phaseLabel.setText("Fortify");
        fortifyRectangle.setOpacity(1);
        attackRectangle.setOpacity(0.5);
        reinforcementRectangle.setOpacity(0.5);
        initPhase();
      }
      case CLAIM_PHASE -> {
        phaseLabel.setText("Claim a territory");
        iconsPane.getChildren().remove(0);
        iconsPane.getChildren().add(phaseLabel);
        StackPane.setAlignment(phaseLabel, Pos.CENTER);


      }
      case ORDER_PHASE -> {
        phaseLabel = new Label("Order");
        attackRectangle.setOpacity(0.5);
        reinforcementRectangle.setOpacity(0.5);
        fortifyRectangle.setOpacity(0.5);
      }
    }
  }

  public void initPhase() {

    VBox iconsAndNameBox = new VBox();
    iconsAndNameBox.setAlignment(Pos.CENTER);
    /*phaseLabel.setStyle(
        "-fx-font-size: 18px; -fx-background-color: white; -fx-font-weight: " + "bold;"); */
    HBox iconsHBox = new HBox();
    iconsHBox.getChildren().addAll(reinforcementRectangle, attackRectangle, fortifyRectangle);
    iconsHBox.setSpacing(25);
    iconsHBox.setAlignment(Pos.CENTER);
    iconsAndNameBox.getChildren().addAll(iconsHBox, phaseLabel);
    if (iconsPane.getChildren().size() > 0) {
      iconsPane.getChildren().remove(0);
    }
    iconsPane.getChildren().add(iconsAndNameBox);


  }
}
