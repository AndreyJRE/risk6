package com.unima.risk6.gui.uimodels;

import com.unima.risk6.game.ai.bots.EasyBot;
import com.unima.risk6.game.ai.bots.HardBot;
import com.unima.risk6.game.ai.bots.MediumBot;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.game.models.enums.GamePhase;
import com.unima.risk6.gui.configurations.ImageConfiguration;
import com.unima.risk6.gui.controllers.GameSceneController;
import com.unima.risk6.gui.controllers.enums.ImageName;
import javafx.application.Platform;
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

/**
 * Represents the graphical user interface (UI) representation of the active player in the bottom
 * area of the game.
 *
 * @author mmeider
 */

public class ActivePlayerUi extends Group {

  private final StackPane iconsPane;

  private StackPane troopsPane;

  private Ellipse ellipse;

  private Rectangle rectangle;

  private PlayerUi playerUi;

  private Label phaseLabel;

  private Rectangle reinforcementRectangle;

  private Rectangle attackRectangle;

  private Rectangle fortifyRectangle;

  private Label userLabel;

  private Rectangle userRectangle;

  private boolean displayDeployable;

  private Label deployableTroops;

  private static final double WIDE_ACTIVE_PLAYER_WIDTH = 390;

  /**
   * Creates a UI representation of the active player.
   *
   * @param radiusX         The X radius of the ellipse.
   * @param radiusY         The Y radius of the ellipse.
   * @param rectangleWidth  The width of the rectangle.
   * @param rectangleHeight The height of the rectangle.
   * @param playerUi        The UI representation of the player.
   */

  public ActivePlayerUi(double radiusX, double radiusY, double rectangleWidth,
      double rectangleHeight, PlayerUi playerUi) {
    this.deployableTroops = new Label("");
    this.playerUi = playerUi;
    this.displayDeployable = true;
    ellipse = new Ellipse(0, 0, radiusX, radiusY);
    ImageView userImage;
    Player player = playerUi.getPlayer();
    if (player instanceof EasyBot) {
      userImage = new ImageView(
          new Image(getClass().getResource("/com/unima/risk6/pictures/easyBot.png").toString()));
    } else if (player instanceof MediumBot) {
      userImage = new ImageView(
          new Image(getClass().getResource("/com/unima/risk6/pictures/mediumBot.png").toString()));
    } else if (player instanceof HardBot) {
      userImage = new ImageView(
          new Image(getClass().getResource("/com/unima/risk6/pictures/hardBot.png").toString()));
    } else {
      userImage = new ImageView(
          new Image(getClass().getResource("/com/unima/risk6/pictures/playerIcon.png").toString()));
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
    troopsPane = new StackPane();
    troopsPane.setAlignment(Pos.CENTER);
    troopsPane.setPrefSize(rectangleWidth - 80, rectangleHeight - 10);
    troopsPane.setLayoutY(5 - rectangleHeight / 2);

    iconsPane = new StackPane();
    iconsPane.setPrefSize(rectangleWidth - 80, rectangleHeight - 10);
    iconsPane.setAlignment(Pos.CENTER);
    iconsPane.setLayoutX(35);
    iconsPane.setLayoutY(5 - rectangleHeight / 2);
    phaseLabel = new Label();
    phaseLabel.setStyle("-fx-font-size: 18px; -fx-background-color: white; -fx-font-weight: bold;");
    phaseLabel.setText("Order");
    Image reinforcementImage = ImageConfiguration.getImageByName(ImageName.REINFORCE_ICON);
    ImagePattern reinforcementImagePattern = new ImagePattern(reinforcementImage);
    reinforcementRectangle = new Rectangle(radiusX, radiusY);
    reinforcementRectangle.setFill(reinforcementImagePattern);
    Image attackImage = ImageConfiguration.getImageByName(ImageName.SWORD_ICON);
    ImagePattern attackImagePattern = new ImagePattern(attackImage);
    attackRectangle = new Rectangle(radiusX, radiusY);
    attackRectangle.setFill(attackImagePattern);
    Image fortifyImage = ImageConfiguration.getImageByName(ImageName.FORTIFY_ICON);
    ImagePattern fortifyImagePattern = new ImagePattern(fortifyImage);
    fortifyRectangle = new Rectangle(radiusX, radiusY);
    fortifyRectangle.setFill(fortifyImagePattern);
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

  /**
   * Changes the UI representation of the active player.
   *
   * @param playerUi The new UI representation of the player.
   */

  public void changeActivePlayerUi(PlayerUi playerUi) {
    if (!this.playerUi.getPlayer().getUser().equals(playerUi.getPlayer().getUser())
        && this.playerUi.getPlayer().getUser()
        .equals(GameSceneController.getPlayerController().getPlayer().getUser())
        && playerUi.getPlayer().getCurrentPhase() != GamePhase.CLAIM_PHASE) {
      //todo change to work
      // SoundConfiguration.playYourTurnSound();
    }
    this.playerUi = playerUi;
    Player player = playerUi.getPlayer();
    ImageView userImage;
    if (player instanceof EasyBot) {
      userImage = new ImageView(
          new Image(getClass().getResource("/com/unima/risk6/pictures/easyBot.png").toString()));
    } else if (player instanceof MediumBot) {
      userImage = new ImageView(
          new Image(getClass().getResource("/com/unima/risk6/pictures/mediumBot.png").toString()));
    } else if (player instanceof HardBot) {
      userImage = new ImageView(
          new Image(getClass().getResource("/com/unima/risk6/pictures/hardBot.png").toString()));
    } else {
      userImage = new ImageView(
          new Image(getClass().getResource("/com/unima/risk6/pictures/playerIcon.png").toString()));
    }
    StackPane stackPane = new StackPane(userImage);
    stackPane.setStyle("-fx-background-color: #F5F5F5;");
    ellipse.setFill(new ImagePattern(stackPane.snapshot(null, null)));
    ellipse.setStroke(playerUi.getPlayerColor());
    rectangle.setStroke(playerUi.getPlayerColor());
    userRectangle.setStroke(playerUi.getPlayerColor());
    userLabel.setText(player.getUser());
    Platform.runLater(this::updateActivePlayerTroops);
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
      default -> {
      }
    }
  }

  /**
   * Initializes the frame of the active player and its different phases.
   */

  public void initPhase() {

    VBox iconsAndNameBox = new VBox();
    iconsAndNameBox.setAlignment(Pos.CENTER);
    HBox iconsHbox = new HBox();
    iconsHbox.getChildren().addAll(reinforcementRectangle, attackRectangle, fortifyRectangle);
    iconsHbox.setSpacing(25);
    iconsHbox.setAlignment(Pos.CENTER);
    iconsAndNameBox.getChildren().addAll(iconsHbox, phaseLabel);
    if (iconsPane.getChildren().size() > 0) {
      iconsPane.getChildren().remove(0);
    }
    iconsPane.getChildren().add(iconsAndNameBox);
  }

  /**
   * Controls the display of the deployable troops by either expanding or contracting the rectangle
   * which creates the foundation of the ActivePlayerUi.
   */

  public void controlDeployableTroops() {
    if (displayDeployable) {
      rectangle.setWidth(WIDE_ACTIVE_PLAYER_WIDTH);
      rectangle.setLayoutX(-129);
      iconsPane.setLayoutX(35);
      Image soldierImage = new Image(
          getClass().getResource("/com/unima/risk6/pictures/soldier.png").toString());
      ImagePattern soldierImagePattern = new ImagePattern(soldierImage);
      Rectangle soldierIcon = new Rectangle(ellipse.getRadiusX(), ellipse.getRadiusY());
      soldierIcon.setFill(soldierImagePattern);
      updateActivePlayerTroops();
      deployableTroops.setStyle("-fx-font-size: 16px;-fx-font-weight: bold;");

      if (troopsPane.getChildren().size() == 0) {
        VBox centeredVbox = new VBox();
        centeredVbox.setAlignment(Pos.CENTER);

        HBox deployableBox = new HBox();
        deployableBox.getChildren().addAll(deployableTroops, soldierIcon);
        deployableBox.setSpacing(8);
        deployableBox.setAlignment(Pos.CENTER);
        centeredVbox.getChildren().add(deployableBox);
        troopsPane.getChildren().add(centeredVbox);
        troopsPane.setLayoutX(-190);
        this.getChildren().add(troopsPane);
      }
      troopsPane.setVisible(true);
    } else {
      rectangle.setWidth(rectangle.getWidth() - 90);
      rectangle.setLayoutX(0);
      iconsPane.setLayoutX(65);
      troopsPane.setVisible(false);
    }
  }

  /**
   * Updates the number of deployable troops for the active player.
   */

  public void updateActivePlayerTroops() {
    Player player = playerUi.getPlayer();
    if (player.getCurrentPhase() == GamePhase.CLAIM_PHASE) {
      deployableTroops.setText(Integer.toString(player.getInitialTroops()));
    } else {
      deployableTroops.setText(Integer.toString(player.getDeployableTroops()));
    }
  }

  public void setDisplayDeployable(boolean displayDeployable) {
    this.displayDeployable = displayDeployable;
  }

  public PlayerUi getPlayerUi() {
    return playerUi;
  }
}
