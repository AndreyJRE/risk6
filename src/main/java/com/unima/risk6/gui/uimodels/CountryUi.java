package com.unima.risk6.gui.uimodels;

import static com.unima.risk6.game.models.enums.GamePhase.ATTACK_PHASE;
import static com.unima.risk6.game.models.enums.GamePhase.FORTIFY_PHASE;
import static com.unima.risk6.game.models.enums.GamePhase.REINFORCEMENT_PHASE;

import com.unima.risk6.game.configurations.GameConfiguration;
import com.unima.risk6.game.logic.Attack;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.logic.Reinforce;
import com.unima.risk6.game.logic.controllers.PlayerController;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.enums.GamePhase;
import com.unima.risk6.gui.controllers.GameSceneController;
import com.unima.risk6.gui.controllers.enums.Colors;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.animation.Animation;
import javafx.animation.FillTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.SVGPath;
import javafx.stage.Popup;
import javafx.util.Duration;

/**
 * The CountryUi class represents the graphical user interface (UI) for a country in the RISK game.
 * It provides methods for updating the country's visual appearance, handling interactions with the
 * country, and displaying related information depending on the game phase.
 *
 * @author mmeider
 * @author astoyano
 */

public class CountryUi extends Group {

  private Country country;

  private Set<CountryUi> adjacentCountryUis;

  private final SVGPath countryPath;

  private final DropShadow glowEffect;

  private TroopsCounterUi troopsCounterUi;

  private static boolean isCountrySelected = false;

  private Color color;

  private FillTransition fillTransition;

  private FillTransition attackingTransition;
  private final Popup popUp;

  /**
   * Constructs a new instance of the `CountryUi` class with the specified country and SVG path.
   *
   * @param country the country associated with this UI element
   * @param svgPath the SVG path representing the shape of the country
   */
  public CountryUi(Country country, String svgPath) {
    super();
    this.country = country;
    this.countryPath = new SVGPath();
    this.troopsCounterUi = null;
    this.countryPath.setContent(svgPath);
    this.color = Colors.COUNTRY_BACKGROUND.getColor();
    this.countryPath.setFill(color);
    this.countryPath.setStroke(Colors.COUNTRY_STROKE.getColor());
    this.getChildren().add(new Group(this.countryPath));
    glowEffect = new DropShadow();
    popUp = new Popup();

  }

  /**
   * Adjusts the placement of the ellipse based on the given country and coordinates.
   *
   * @param country  the country to adjust the ellipse placement for
   * @param ellipseX the X-coordinate of the ellipse
   * @param ellipseY the Y-coordinate of the ellipse
   * @return the adjusted coordinates as a `Point2D` object
   */
  public static Point2D correctEllipsePlacement(Country country, double ellipseX, double ellipseY) {
    String countryName = country.getCountryName().name();

    switch (countryName) {
      case "ARGENTINA" -> ellipseX -= 15;
      case "BRAZIL" -> ellipseX += 20;
      case "PERU" -> {
        ellipseX -= 10;
        ellipseY += 2;
      }
      case "VENEZUELA" -> {
        ellipseY -= 5;
        ellipseX -= 5;
      }
      case "NORTH_WEST_TERRITORY" -> ellipseY += 15;
      case "ALASKA" -> ellipseY -= 10;
      case "URAL" -> ellipseX -= 10;
      case "KAMCHATKA" -> ellipseY -= 25;
      case "SIBERIA" -> ellipseX += 10;
      case "JAPAN" -> ellipseX += 5;
      case "EAST_AFRICA" -> ellipseX += 5;
      default -> {
      }
    }
    return new Point2D(ellipseX, ellipseY);
  }

  /**
   * Initializes the mouse listener for the country UI element.
   */
  public void initMouseListener() {
    this.hoverProperty().addListener((observable, oldValue, newValue) -> {
      PlayerController playerController = GameSceneController.getPlayerController();
      GamePhase currentPhase = playerController.getPlayer().getCurrentPhase();
      if (newValue) {
        switch (currentPhase) {
          case CLAIM_PHASE -> {

            int numberOfNeutralCountries = GameConfiguration.getGameState().getCountries().stream()
                .filter(country1 -> !country1.hasPlayer()).toList().size();
            if ((checkIfCountryIsMine(country) && numberOfNeutralCountries == 0)
                || !country.hasPlayer()) {
              if (GameConfiguration.getTutorial() != null) {
                Country claimTutorialCountry = GameConfiguration.getTutorial().getCurrentClaim()
                    .getCountry();
                if (this.country.equals(claimTutorialCountry)) {
                  this.countryPath.setFill(Color.LIGHTGRAY);
                  this.setCursor(Cursor.CROSSHAIR);
                }
              } else {
                if (this.color == Colors.COUNTRY_BACKGROUND.getColor()) {
                  this.countryPath.setFill(Colors.COUNTRY_BACKGROUND_DARKEN.getColor());
                }
                this.setCursor(Cursor.CROSSHAIR);
              }
            }
          }
          case ATTACK_PHASE, FORTIFY_PHASE -> {
            if (checkIfCountryIsMine(country) && country.getTroops() > 1) {
              if (GameConfiguration.getTutorial() != null) {
                Attack currentAttack = GameConfiguration.getTutorial().getCurrentAttack();
                Country attackCountry = currentAttack.getAttackingCountry();
                if (this.country.equals(attackCountry)) {
                  this.setCursor(Cursor.CROSSHAIR);
                }
              } else {
                this.setCursor(Cursor.CROSSHAIR);
              }
            }
          }
          case REINFORCEMENT_PHASE -> {
            if (checkIfCountryIsMine(country)
                && playerController.getPlayer().getDeployableTroops() > 0) {
              if (GameConfiguration.getTutorial() != null) {
                Reinforce currentReinforce = GameConfiguration.getTutorial().getCurrentReinforce();
                Country reinforceCountry = currentReinforce.getCountry();
                if (this.country.equals(reinforceCountry)) {
                  this.setCursor(Cursor.CROSSHAIR);
                }
              } else {
                this.setCursor(Cursor.CROSSHAIR);
              }
            }
          }
          default -> {
          }
        }
      } else {
        this.countryPath.setFill(color);
        this.setCursor(Cursor.DEFAULT);
      }
    });

    setOnMouseClicked(event -> {

      Group countriesGroup = (Group) this.getParent();
      PlayerController playerController = GameSceneController.getPlayerController();
      GamePhase currentPhase = playerController.getPlayer().getCurrentPhase();
      switch (currentPhase) {
        case CLAIM_PHASE -> {
          int numberOfNeutralCountries = GameConfiguration.getGameState().getCountries().stream()
              .filter(country -> !country.hasPlayer()).toList().size();
          if ((checkIfCountryIsMine(country) && numberOfNeutralCountries == 0)
              || !country.hasPlayer() && event.getClickCount() == 1) {
            if (GameConfiguration.getTutorial() != null) {
              Reinforce currentClaim = GameConfiguration.getTutorial().getCurrentClaim();
              Country claimTutorialCountry = currentClaim.getCountry();
              if (this.country.equals(claimTutorialCountry)) {
                playerController.sendReinforce(this.getCountry(), 1);
                playerController.sendEndPhase(currentPhase);
                fillTransition.stop();
              }
            } else {
              playerController.sendReinforce(this.country, 1);
              playerController.sendEndPhase(currentPhase);
            }
          }
        }
        case ATTACK_PHASE -> {
          if (checkIfCountryIsMine(country) && country.getTroops() > 1) {
            if (GameConfiguration.getTutorial() != null) {
              Attack currentAttack = GameConfiguration.getTutorial().getCurrentAttack();
              Country attackCountry = currentAttack.getAttackingCountry();
              if (this.country.equals(attackCountry)) {
                animateAttackPhase(countriesGroup);
                fillTransition.stop();
              }
            } else {
              animateAttackPhase(countriesGroup);
            }
          }
        }
        case FORTIFY_PHASE -> {
          if (checkIfCountryIsMine(country) && country.getTroops() > 1) {
            if (GameConfiguration.getTutorial() != null) {
              Fortify currentFortify = GameConfiguration.getTutorial().getCurrentFortify();
              Country fortifyCountry = currentFortify.getOutgoing();
              if (this.country.equals(fortifyCountry)) {
                animateFortifyPhase(countriesGroup);
                fillTransition.stop();
              }
            } else {
              animateFortifyPhase(countriesGroup);
            }
          }
        }
        case REINFORCEMENT_PHASE -> {
          if (checkIfCountryIsMine(country)
              && playerController.getPlayer().getDeployableTroops() > 0
              && event.getClickCount() == 1) {
            if (GameConfiguration.getTutorial() != null) {
              Reinforce currentReinforce = GameConfiguration.getTutorial().getCurrentReinforce();
              Country reinforceCountry = currentReinforce.getCountry();
              if (this.country.equals(reinforceCountry)) {
                animateReinforcementPhase();
                fillTransition.stop();
              }
            } else {
              animateReinforcementPhase();
            }
          }
        }
        default -> {
        }
      }
      this.setCursor(Cursor.DEFAULT);
    });
  }

  /**
   * Creates a clone of the given SVG path.
   *
   * @param original the original SVG path to clone
   * @return a new instance of `SVGPath` with the same content and properties as the original
   */
  public SVGPath svgPathClone(SVGPath original) {
    SVGPath clone = new SVGPath();
    clone.setContent(original.getContent());
    clone.setFill(original.getFill());
    clone.setStroke(original.getStroke());
    clone.setStrokeWidth(original.getStrokeWidth());
    clone.setLayoutX(original.getLayoutX());
    clone.setLayoutY(original.getLayoutY());
    return clone;
  }

  /**
   * Adds event handlers to the adjacent country path for the specified country UI element and game
   * phase.
   *
   * @param adjacentCountryPath the SVG path representing the adjacent country
   * @param adjacentCountryUi   the adjacent country UI element
   * @param gamePhase           the current game phase
   */
  public void addEventHandlersToAdjacentCountryPath(SVGPath adjacentCountryPath,
      CountryUi adjacentCountryUi, GamePhase gamePhase) {
    adjacentCountryPath.setOnMouseEntered(mouseEvent -> adjacentCountryPath.setCursor(Cursor.HAND));
    adjacentCountryPath.setOnMouseClicked(event -> {
      if (event.getClickCount() == 1 && !popUp.isShowing()) {
        if (gamePhase == ATTACK_PHASE) {
          showAmountOfTroopsPopUp(Math.min(3, this.getCountry().getTroops() - 1), adjacentCountryUi,
              gamePhase);
        } else {
          showAmountOfTroopsPopUp(this.country.getTroops() - 1, adjacentCountryUi, gamePhase);
        }
      }
    });
  }


  /**
   * Animates the attack phase by showing the possible attack paths and highlighting adjacent
   * countries.
   *
   * @param countriesGroup the parent group of the country UI elements
   */
  public void animateAttackPhase(Group countriesGroup) {
    if (isCountrySelected) {
      removeArrowsAndAdjacentCountries();
      setCursor(Cursor.DEFAULT);
    } else {
      for (CountryUi adjacentCountryUi : adjacentCountryUis) {
        if (!checkIfCountryIsMine(adjacentCountryUi.getCountry())) {
          Line arrow = createArrowAndAnimateAdjacentCountries(countriesGroup, adjacentCountryUi,
              ATTACK_PHASE);
          animateArrow(adjacentCountryUi, arrow);
        }
      }
      isCountrySelected = true;
    }
  }

  /**
   * Animates the reinforcement phase by showing the pop-up window for selecting the number of
   * troops to reinforce.
   */
  private void animateReinforcementPhase() {
    showAmountOfTroopsPopUp(
        GameSceneController.getPlayerController().getPlayer().getDeployableTroops(), null,
        REINFORCEMENT_PHASE);
  }


  /**
   * Animates the fortify phase by showing the possible fortification paths and highlighting
   * adjacent countries.
   *
   * @param countriesGroup the parent group of the country UI elements
   */
  private void animateFortifyPhase(Group countriesGroup) {
    if (isCountrySelected) {
      removeArrowsAndAdjacentCountries();
      setCursor(Cursor.DEFAULT);
    } else {
      for (CountryUi adjacentCountryUi : adjacentCountryUis) {
        if (checkIfCountryIsMine(adjacentCountryUi.getCountry())) {
          Line arrow = createArrowAndAnimateAdjacentCountries(countriesGroup, adjacentCountryUi,
              FORTIFY_PHASE);
          animateArrow(adjacentCountryUi, arrow);
        }

      }
      isCountrySelected = true;
    }
  }

  /**
   * Shows a pop-up window for selecting the number of troops to move and performs the corresponding
   * action based on the game phase.
   *
   * @param troopBound        the maximum number of troops that can be moved
   * @param adjacentCountryUi the adjacent country UI element
   * @param gamePhase         the current game phase
   */
  public void showAmountOfTroopsPopUp(int troopBound, CountryUi adjacentCountryUi,
      GamePhase gamePhase) {
    AtomicInteger amountOfTroops = new AtomicInteger(1);
    Label chatLabel = new Label("Amount of Troops: " + amountOfTroops);
    chatLabel.setStyle("-fx-font-size: 18px;");

    Button closeAmountOfTroopsButton = new Button();
    closeAmountOfTroopsButton.setPrefSize(20, 20);
    ImageView closeIcon = new ImageView(
        new Image(getClass().getResource("/com/unima/risk6/pictures/closeIcon.png").toString()));
    closeIcon.setFitWidth(20);
    closeIcon.setFitHeight(20);
    closeAmountOfTroopsButton.setGraphic(closeIcon);
    closeAmountOfTroopsButton.setStyle(
        "-fx-background-color: rgba(255, 255, 255, 0.7);" + "-fx-background-radius: 10px;");
    closeAmountOfTroopsButton.setFocusTraversable(false);
    BorderPane moveTroopsPane = new BorderPane();
    moveTroopsPane.setTop(closeAmountOfTroopsButton);
    BorderPane.setAlignment(closeAmountOfTroopsButton, Pos.TOP_RIGHT);

    HBox chatBox = new HBox();
    chatBox.setAlignment(Pos.CENTER);
    chatBox.setSpacing(15);

    closeAmountOfTroopsButton.setOnAction(event -> popUp.hide());

    Circle leftCircle = new Circle(25);
    Image leftImage = new Image(
        Objects.requireNonNull(getClass()
                .getResource("/com/unima/risk6/pictures/minusIcon.png"))
            .toString());
    leftCircle.setFill(new ImagePattern(leftImage));
    leftCircle.setOnMouseClicked(minusEvent -> {
      if (amountOfTroops.get() > 1) {
        amountOfTroops.getAndDecrement();
        chatLabel.setText("Amount of Troops: " + amountOfTroops.get());
      }
    });

    Circle rightCircle = new Circle(25);
    Image rightImage = new Image(
        Objects.requireNonNull(getClass()
                .getResource("/com/unima/risk6/pictures/plusIcon.png"))
            .toString());
    rightCircle.setFill(new ImagePattern(rightImage));
    rightCircle.setOnMouseClicked(plusEvent -> {
      if (amountOfTroops.get() < troopBound) {
        amountOfTroops.getAndIncrement();
        chatLabel.setText("Amount of Troops: " + amountOfTroops.get());
      }
    });

    moveTroopsPane.setOnKeyPressed(event -> {
      switch (event.getCode()) {
        case LEFT -> {
          if (amountOfTroops.get() > 1) {
            amountOfTroops.getAndDecrement();
            chatLabel.setText("Amount of Troops: " + amountOfTroops.get());
          }
        }
        case RIGHT -> {
          if (amountOfTroops.get() < troopBound) {
            amountOfTroops.getAndIncrement();
            chatLabel.setText("Amount of Troops: " + amountOfTroops.get());
          }
        }
        case ENTER -> {
          popUp.hide();
          PlayerController playerController = GameSceneController.getPlayerController();
          if (gamePhase == FORTIFY_PHASE) {
            playerController.sendFortify(country, adjacentCountryUi.getCountry(),
                amountOfTroops.get());

            if (playerController.getPlayer().getCurrentPhase() == FORTIFY_PHASE) {
              playerController.sendEndPhase(gamePhase);
              removeArrowsAndAdjacentCountries();
            }

          } else if (gamePhase == ATTACK_PHASE) {
            playerController.sendAttack(country, adjacentCountryUi.getCountry(),
                amountOfTroops.get());
            removeArrowsAndAdjacentCountries();
          } else if (gamePhase == REINFORCEMENT_PHASE) {
            playerController.sendReinforce(country, amountOfTroops.get());
            try {
              Thread.sleep(100);
            } catch (InterruptedException e) {
              throw new RuntimeException(e);
            }
            if (playerController.getPlayer().getDeployableTroops() == 0
                && !playerController.getHandController().holdsExchangeable()) {
              playerController.sendEndPhase(gamePhase);
            }
          }
        }
        default -> {
        }
      }
    });

    Circle confirmCircle = new Circle(25);
    Image confirmImage = new Image(
        Objects.requireNonNull(getClass()
                .getResource("/com/unima/risk6/pictures/confirmIcon.png"))
            .toString());
    confirmCircle.setFill(new ImagePattern(confirmImage));
    confirmCircle.setOnMouseClicked(confirmEvent -> {
      popUp.hide();
      PlayerController playerController = GameSceneController.getPlayerController();
      if (gamePhase == FORTIFY_PHASE) {
        playerController.sendFortify(this.country, adjacentCountryUi.getCountry(),
            amountOfTroops.get());

        if (playerController.getPlayer().getCurrentPhase() == FORTIFY_PHASE) {
          playerController.sendEndPhase(gamePhase);
          removeArrowsAndAdjacentCountries();
        }

      } else if (gamePhase == ATTACK_PHASE) {
        playerController.sendAttack(this.country, adjacentCountryUi.getCountry(),
            amountOfTroops.get());
        removeArrowsAndAdjacentCountries();
      } else if (gamePhase == REINFORCEMENT_PHASE) {
        playerController.sendReinforce(this.country, amountOfTroops.get());
        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
        if (playerController.getPlayer().getDeployableTroops() == 0
            && !playerController.getHandController().holdsExchangeable()) {
          playerController.sendEndPhase(gamePhase);
        }
      }

    });

    chatBox.getChildren().addAll(leftCircle, chatLabel, rightCircle, confirmCircle);
    HBox.setHgrow(confirmCircle, Priority.ALWAYS);

    BorderPane gamePane = (BorderPane) this.getParent().getParent().getParent();

    moveTroopsPane.setCenter(chatBox);
    moveTroopsPane.setPrefSize(gamePane.getWidth() * 0.30, gamePane.getHeight() * 0.15);
    moveTroopsPane.setStyle(
        "-fx-background-color: rgba(255, 255, 255, 0.7); -fx-background-radius: 10;");

    popUp.getContent().clear();
    popUp.getContent().add(moveTroopsPane);

    moveTroopsPane.setFocusTraversable(true);
    Bounds rootBounds = gamePane.localToScreen(gamePane.getBoundsInLocal());

    double centerX = rootBounds.getMinX() + rootBounds.getWidth() / 2;
    double centerY = rootBounds.getMinY() + rootBounds.getHeight() / 2;
    double popupWidth = moveTroopsPane.getPrefWidth();
    double popupHeight = moveTroopsPane.getPrefHeight();

    popUp.setX(centerX - popupWidth / 2);
    popUp.setY(centerY - popupHeight / 2);
    popUp.show(gamePane.getScene().getWindow());
    Platform.runLater(moveTroopsPane::requestFocus);
  }

  /**
   * Removes the arrow and adjacent countries from the UI.
   */
  public void removeArrowsAndAdjacentCountries() {
    Group countriesGroup = (Group) this.getParent();
    countriesGroup.getChildren().removeIf(countriesGroupNode -> countriesGroupNode instanceof Line
        || countriesGroupNode instanceof SVGPath);
    isCountrySelected = false;
  }

  /**
   * Displays a pop-up window showing the roll and result of an attack.
   *
   * @param lastAttack     the details of the last attack
   * @param attacker       the UI representation of the attacking country
   * @param defender       the UI representation of the defending country
   * @param activePlayerUi the UI representation of the active player
   */
  private void showAttackDicePopUp(Attack lastAttack, CountryUi attacker, CountryUi defender,
      ActivePlayerUi activePlayerUi) {
    HBox diceHbox = new HBox();
    diceHbox.setAlignment(Pos.CENTER);
    diceHbox.setSpacing(20);

    List<DiceUi> diceUis = new ArrayList<>();
    VBox attackerBox = new VBox();

    Label attackingCountry = new Label(
        attacker.country.getCountryName().name().replaceAll("_", " "));
    attackingCountry.setStyle("-fx-font-size: 18px;");
    attackingCountry.setPadding(new Insets(0, 0, 15, 0));
    attackerBox.getChildren().add(attackingCountry);

    for (int i = 0; i < lastAttack.getAttackDiceResult().size(); i++) {
      DiceUi dice = new DiceUi(true, lastAttack.getAttackDiceResult().get(i));
      attackerBox.getChildren().add(dice);
      diceUis.add(dice);
    }
    attackerBox.setAlignment(Pos.CENTER);

    VBox defenderBox = new VBox();
    Label defenderCountry = new Label(
        defender.country.getCountryName().name().replaceAll("_", " "));
    defenderCountry.setStyle("-fx-font-size: 18px;");
    defenderCountry.setPadding(new Insets(0, 0, 15, 0));
    defenderBox.getChildren().add(defenderCountry);

    for (int i = 0; i < lastAttack.getDefendDiceResult().size(); i++) {
      DiceUi dice = new DiceUi(false, lastAttack.getDefendDiceResult().get(i));
      defenderBox.getChildren().add(dice);
      diceUis.add(dice);
    }
    defenderBox.setAlignment(Pos.CENTER);
    diceHbox.getChildren().addAll(attackerBox, defenderBox);
    Popup dicePopup = new Popup();

    PauseTransition delayTransition = new PauseTransition(Duration.millis(2000));
    delayTransition.setOnFinished(delayTransitionEvent -> {
      dicePopup.hide();
      attacker.getAttackingTransition().stop();
      defender.getAttackingTransition().stop();
      if (attacker.getCountry().getTroops() > 1 && lastAttack.getHasConquered()
          && activePlayerUi.getPlayerUi().getPlayer()
          .equals(GameSceneController.getPlayerController().getPlayer())) {
        if (!GameConfiguration.getGameState().isGameOver()) {
          attacker.showAmountOfTroopsPopUp(
              lastAttack.getAttackingCountry().getTroops() - 1 - lastAttack.getTroopNumber(),
              defender, FORTIFY_PHASE);
        }
      }
    });
    BorderPane dicePane = new BorderPane();
    dicePane.setCenter(diceHbox);
    BorderPane gamePane = (BorderPane) this.getParent().getParent().getParent();
    dicePane.setPrefSize(gamePane.getWidth() * 0.50, gamePane.getHeight() * 0.50);
    dicePane.setStyle("-fx-background-color: rgba(255, 255, 255, 0.7); -fx-background-radius: 10;");

    Bounds rootBounds = gamePane.localToScreen(gamePane.getBoundsInLocal());

    double centerX = rootBounds.getMinX() + rootBounds.getWidth() / 2;
    double centerY = rootBounds.getMinY() + rootBounds.getHeight() / 2;

    double popupWidth = dicePane.getPrefWidth();
    double popupHeight = dicePane.getPrefHeight();

    dicePopup.getContent().add(dicePane);

    dicePopup.setX(centerX - popupWidth / 2);
    dicePopup.setY(centerY - popupHeight / 2);
    dicePopup.show(gamePane.getScene().getWindow());
    for (DiceUi dice : diceUis) {
      dice.rollDice();
    }
    attacker.animateInAttackActiveCountry(activePlayerUi.getPlayerUi().getPlayerColor());
    if (lastAttack.getHasConquered()) {
      defender.animateInAttackActiveCountry(activePlayerUi.getPlayerUi().getPlayerColor());
    } else {
      defender.animateInAttackActiveCountry((Color) defender.getCountryPath().getFill());
    }
    delayTransition.play();
  }

  /**
   * Creates an arrow and animates it between the current country and an adjacent country.
   *
   * @param countriesGroup    the parent group containing all country UI elements
   * @param adjacentCountryUi the UI representation of the adjacent country
   * @param gamePhase         the current phase of the game
   * @return the created arrow line
   */
  private Line createArrowAndAnimateAdjacentCountries(Group countriesGroup,
      CountryUi adjacentCountryUi, GamePhase gamePhase) {
    SVGPath adjacentCountryPath = svgPathClone(adjacentCountryUi.getCountryPath());
    adjacentCountryPath.setEffect(adjacentCountryUi.getGlowEffect());
    addEventHandlersToAdjacentCountryPath(adjacentCountryPath, adjacentCountryUi, gamePhase);
    Line arrow = new Line();
    arrow.setStroke(Color.RED.invert());
    int index = 0;
    for (Node troopsCounterUiNode : countriesGroup.getChildren()) {
      if (troopsCounterUiNode instanceof TroopsCounterUi) {
        countriesGroup.getChildren().add(index, adjacentCountryPath);
        countriesGroup.getChildren().add(index, arrow);
        break;
      } else {
        index++;
      }
    }
    return arrow;
  }


  /**
   * Animates the arrow line between the current country and an adjacent country.
   *
   * @param adjacentCountryUi the UI representation of the adjacent country
   * @param arrow             the arrow line to animate
   */
  private void animateArrow(CountryUi adjacentCountryUi, Line arrow) {
    Point2D clickPosInScene = this.localToScene(
        this.getTroopsCounterUi().getEllipseCounter().getCenterX(),
        this.getTroopsCounterUi().getEllipseCounter().getCenterY());
    Point2D clickPosInSceneToCountry = adjacentCountryUi.localToScene(
        adjacentCountryUi.getTroopsCounterUi().getEllipseCounter().getCenterX(),
        adjacentCountryUi.getTroopsCounterUi().getEllipseCounter().getCenterY());
    Point2D clickPosInGroup = this.sceneToLocal(clickPosInScene);
    Point2D clickPosInGroupToCountry = this.sceneToLocal(clickPosInSceneToCountry);
    arrow.setStartX(clickPosInGroup.getX());
    arrow.setStartY(clickPosInGroup.getY());
    arrow.setEndX(clickPosInGroup.getX());
    KeyValue endXvalue = new KeyValue(arrow.endXProperty(), clickPosInGroupToCountry.getX());
    arrow.setEndY(clickPosInGroup.getY());
    KeyValue endYvalue = new KeyValue(arrow.endYProperty(), clickPosInGroupToCountry.getY());
    setCursor(Cursor.MOVE);

    Timeline timeline = new Timeline();
    KeyFrame keyFrame = new KeyFrame(Duration.millis(600), endXvalue, endYvalue);

    timeline.getKeyFrames().add(keyFrame);
    timeline.setCycleCount(1);
    timeline.setAutoReverse(false);
    timeline.play();
  }

  /**
   * Updates the visual appearance of the country based on the active player and game state.
   *
   * @param activePlayerUi the UI representation of the active player
   */
  public void update(ActivePlayerUi activePlayerUi) {
    troopsCounterUi.update(country.getTroops());
    Color playerColor = activePlayerUi.getPlayerUi().getPlayerColor();
    Color countryPathColor = (Color) this.countryPath.getFill();
    if (playerColor.equals(countryPathColor)) {
      animateActiveCountry(playerColor);
      return;
    }
    FillTransition highlightTransition = new FillTransition(Duration.seconds(0.2), this.countryPath,
        countryPathColor, playerColor);
    highlightTransition.setInterpolator(Interpolator.EASE_BOTH);
    glowEffect.setColor(playerColor);
    highlightTransition.play();
    this.color = playerColor;
  }

  /**
   * Animates the visual appearance of the country as the active country.
   *
   * @param currentColor the current color of the country
   */
  private void animateActiveCountry(Color currentColor) {
    Color brightHighlightColor = currentColor.deriveColor(0, 0, 0, 0.8);
    FillTransition highlightTransition = new FillTransition(Duration.seconds(0.2), this.countryPath,
        currentColor, brightHighlightColor);
    highlightTransition.setInterpolator(Interpolator.EASE_BOTH);
    FillTransition revertTransition = new FillTransition(Duration.seconds(0.2), this.countryPath,
        brightHighlightColor, currentColor);
    revertTransition.setInterpolator(Interpolator.EASE_BOTH);
    SequentialTransition sequentialTransition = new SequentialTransition(highlightTransition,
        revertTransition);
    sequentialTransition.play();
  }

  /**
   * Animates the visual appearance of the country during an attack.
   *
   * @param currentCountryColor the current color of the country
   */
  private void animateInAttackActiveCountry(Color currentCountryColor) {
    Color brightHighlightColor = currentCountryColor.deriveColor(0, 0, 0, 0.7);
    attackingTransition = new FillTransition(Duration.seconds(0.25), this.countryPath,
        currentCountryColor, brightHighlightColor);
    attackingTransition.setInterpolator(Interpolator.EASE_BOTH);
    attackingTransition.setAutoReverse(true);
    attackingTransition.setCycleCount(Animation.INDEFINITE);
    attackingTransition.play();
  }


  /**
   * Updates the UI after an attack occurs, including displaying a pop-up and updating troop
   * counts.
   *
   * @param activePlayerUi the UI representation of the active player
   * @param attack         the details of the attack
   * @param attacker       the UI representation of the attacking country
   * @param defender       the UI representation of the defending country
   */
  public void updateAfterAttack(ActivePlayerUi activePlayerUi, Attack attack, CountryUi attacker,
      CountryUi defender) {
    showAttackDicePopUp(attack, attacker, defender, activePlayerUi);
    if (attack.getHasConquered()) {
      Color playerColor = activePlayerUi.getPlayerUi().getPlayerColor();
      FillTransition highlightTransition = new FillTransition(Duration.seconds(1),
          attacker.getCountryPath(), (Color) attacker.getCountryPath().getFill(), playerColor);
      highlightTransition.setInterpolator(Interpolator.EASE_BOTH);
      highlightTransition.play();
      this.color = playerColor;
      if (GameSceneController.getMyPlayerUi().getPlayer()
          .equals(activePlayerUi.getPlayerUi().getPlayer())) {
        GameSceneController.getPlayerController()
            .sendFortify(attack.getAttackingCountry(), attack.getDefendingCountry(),
                attack.getTroopNumber());


      }
    }
    attacker.getTroopsCounterUi().update(attack.getAttackingCountry().getTroops());
    defender.getTroopsCounterUi().update(attack.getDefendingCountry().getTroops());


  }


  /**
   * Checks if the given country belongs to the current player.
   *
   * @param country the country to check
   * @return {@code true} if the country belongs to the current player, {@code false} otherwise
   */
  public boolean checkIfCountryIsMine(Country country) {
    if (country.getPlayer() == null) {
      return false;
    }
    return country.getPlayer().equals(GameSceneController.getPlayerController().getPlayer());
  }


  /**
   * Animates the country to indicate it as part of a tutorial by using FillTransition animations.
   */
  public void animateTutorialCountry() {
    fillTransition = new FillTransition(Duration.seconds(0.5));
    fillTransition.setShape(this.getCountryPath());
    fillTransition.setFromValue((Color) this.getCountryPath().getFill());
    fillTransition.setToValue(Color.rgb(128, 50, 189));
    fillTransition.setCycleCount(Animation.INDEFINITE);
    fillTransition.setAutoReverse(true);
    fillTransition.play();
  }

  public SVGPath getCountryPath() {
    return countryPath;
  }

  public Country getCountry() {
    return country;
  }

  public TroopsCounterUi getTroopsCounterUi() {
    return troopsCounterUi;
  }

  public Set<CountryUi> getAdjacentCountryUis() {
    return adjacentCountryUis;
  }

  public DropShadow getGlowEffect() {
    return glowEffect;
  }

  public FillTransition getAttackingTransition() {
    return attackingTransition;
  }

  public void setCountry(Country country) {
    this.country = country;
  }

  public void setColor(Color color) {
    this.color = color;
  }

  public void setTroopsCounterUi(TroopsCounterUi troopsCounterUi) {
    this.troopsCounterUi = troopsCounterUi;
  }

  public void setAdjacentCountryUis(Set<CountryUi> adjacentCountryUis) {
    this.adjacentCountryUis = adjacentCountryUis;
  }
}