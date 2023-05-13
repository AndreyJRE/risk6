package com.unima.risk6.gui.uiModels;

import static com.unima.risk6.game.models.enums.GamePhase.ATTACK_PHASE;
import static com.unima.risk6.game.models.enums.GamePhase.FORTIFY_PHASE;
import static com.unima.risk6.game.models.enums.GamePhase.REINFORCEMENT_PHASE;

import com.unima.risk6.game.ai.tutorial.Tutorial;
import com.unima.risk6.game.configurations.GameConfiguration;
import com.unima.risk6.game.logic.Attack;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.logic.Reinforce;
import com.unima.risk6.game.logic.controllers.PlayerController;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.enums.GamePhase;
import com.unima.risk6.gui.controllers.GameSceneController;
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

public class CountryUi extends Group {

  private Country country;

  private Set<CountryUi> adjacentCountryUis;

  private final SVGPath countryPath;

  private final DropShadow glowEffect;

  private TroopsCounterUi troopsCounterUi;

  private static boolean isCountrySelected = false;

  private Color color;

  private Tutorial tutorial = GameConfiguration.getTutorial();
  private FillTransition fillTransition;


  public CountryUi(Country country, String SVGPath) {
    super();
    this.country = country;
    this.countryPath = new SVGPath();
    this.troopsCounterUi = null;
    this.countryPath.setContent(SVGPath);
    this.color = Color.WHITE;
    this.countryPath.setFill(color);
    this.countryPath.setStroke(Color.BLACK);
    this.getChildren().add(new Group(this.countryPath));
    glowEffect = new DropShadow();

  }

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
              if (tutorial != null) {
                Country claimTutorialCountry = tutorial.getCurrentClaim().getCountry();
                if (this.country.equals(claimTutorialCountry)) {
                  this.countryPath.setFill(Color.LIGHTGRAY);
                  this.setCursor(Cursor.CROSSHAIR);
                }
              } else {
                if (this.color == Color.WHITE) {
                  this.countryPath.setFill(Color.LIGHTGRAY);
                }
                this.setCursor(Cursor.CROSSHAIR);
              }
            }
          }
          case ATTACK_PHASE, FORTIFY_PHASE -> {
            if (checkIfCountryIsMine(country) && country.getTroops() > 1) {
              if (tutorial != null) {
                Attack currentAttack = tutorial.getCurrentAttack();
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
              if ((tutorial = GameConfiguration.getTutorial()) != null) {
                Reinforce currentReinforce = tutorial.getCurrentReinforce();
                Country reinforceCountry = currentReinforce.getCountry();
                if (this.country.equals(reinforceCountry)) {
                  this.setCursor(Cursor.CROSSHAIR);
                }
              } else {
                this.setCursor(Cursor.CROSSHAIR);
              }
            }

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
              || !country.hasPlayer()) {
            if (tutorial != null) {
              Reinforce currentClaim = tutorial.getCurrentClaim();
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
            if (tutorial != null) {
              Attack currentAttack = tutorial.getCurrentAttack();
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
            if (tutorial != null) {
              Fortify currentFortify = tutorial.getCurrentFortify();
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
              && playerController.getPlayer().getDeployableTroops() > 0) {
            if (tutorial != null) {
              Reinforce currentReinforce = tutorial.getCurrentReinforce();
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

        // add more cases for other enum values
      }
      this.setCursor(Cursor.DEFAULT);


    });
  }

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

  public void addEventHandlersToAdjacentCountryPath(SVGPath adjacentCountryPath,
      CountryUi adjacentCountryUi, GamePhase gamePhase) {
    adjacentCountryPath.setOnMouseEntered(mouseEvent -> adjacentCountryPath.setCursor(Cursor.HAND));
    adjacentCountryPath.setOnMouseClicked(event -> {
      if (gamePhase == ATTACK_PHASE) {
        showAmountOfTroopsPopUp(Math.min(3, this.getCountry().getTroops() - 1), adjacentCountryUi,
            gamePhase);
      } else {
        showAmountOfTroopsPopUp(this.country.getTroops() - 1, adjacentCountryUi, gamePhase);
      }

    });
  }

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

  private void animateReinforcementPhase() {
    showAmountOfTroopsPopUp(
        GameSceneController.getPlayerController().getPlayer().getDeployableTroops(), null,
        REINFORCEMENT_PHASE);
  }

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

  public void showAmountOfTroopsPopUp(int troopBound, CountryUi adjacentCountryUi,
      GamePhase gamePhase) {
    AtomicInteger amountOfTroops = new AtomicInteger(1);
    BorderPane gamePane = (BorderPane) this.getParent().getParent().getParent();
    BorderPane moveTroopsPane = new BorderPane();
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
        "-fx-background-color: rgba(255, 255, 255, 0.3);" + "-fx-background-radius: 10px;");
    closeAmountOfTroopsButton.setFocusTraversable(false);

    moveTroopsPane.setTop(closeAmountOfTroopsButton);
    BorderPane.setAlignment(closeAmountOfTroopsButton, Pos.TOP_RIGHT);

    HBox chatBox = new HBox();
    chatBox.setAlignment(Pos.CENTER);
    chatBox.setSpacing(15);

    Popup popUp = new Popup();

    closeAmountOfTroopsButton.setOnAction(event -> popUp.hide());

    Circle leftCircle = new Circle(25);
    Image leftImage = new Image(
        Objects.requireNonNull(getClass().getResource("/com/unima/risk6/pictures/minusIcon.png"))
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
        Objects.requireNonNull(getClass().getResource("/com/unima/risk6/pictures/plusIcon.png"))
            .toString());
    rightCircle.setFill(new ImagePattern(rightImage));
    rightCircle.setOnMouseClicked(plusEvent -> {
      if (amountOfTroops.get() < troopBound) {
        amountOfTroops.getAndIncrement();
        chatLabel.setText("Amount of Troops: " + amountOfTroops.get());
      }
    });

    Circle confirmCircle = new Circle(25);
    Image confirmImage = new Image(
        Objects.requireNonNull(getClass().getResource("/com/unima/risk6/pictures/confirmIcon.png"))
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

    moveTroopsPane.setCenter(chatBox);
    moveTroopsPane.setPrefSize(gamePane.getWidth() * 0.40, gamePane.getHeight() * 0.20);
    moveTroopsPane.setStyle(
        "-fx-background-color: rgba(255, 255, 255, 0.3); -fx-background-radius: 10;");

    Bounds rootBounds = gamePane.localToScreen(gamePane.getBoundsInLocal());

    double centerX = rootBounds.getMinX() + rootBounds.getWidth() / 2;
    double centerY = rootBounds.getMinY() + rootBounds.getHeight() / 2;

    double popupWidth = moveTroopsPane.getPrefWidth();
    double popupHeight = moveTroopsPane.getPrefHeight();

    popUp.getContent().add(moveTroopsPane);

    popUp.setX(centerX - popupWidth / 2);
    popUp.setY(centerY - popupHeight / 2);
    popUp.show(gamePane.getScene().getWindow());
  }

  public void removeArrowsAndAdjacentCountries() {
    Group countriesGroup = (Group) this.getParent();
    countriesGroup.getChildren().removeIf(countriesGroupNode -> countriesGroupNode instanceof Line
        || countriesGroupNode instanceof SVGPath);
    isCountrySelected = false;
  }

  private void showAttackDicePopUp(Attack lastAttack, CountryUi attacker, CountryUi defender,
      ActivePlayerUi activePlayerUi) {
    BorderPane gamePane = (BorderPane) this.getParent().getParent().getParent();
    BorderPane dicePane = new BorderPane();

    HBox diceHBox = new HBox();
    diceHBox.setAlignment(Pos.CENTER);
    diceHBox.setSpacing(20);
    Popup dicePopup = new Popup();

    List<DiceUi> diceUis = new ArrayList<>();
    VBox attackerBox = new VBox();
    Label attackerUser = new Label(attacker.country.getPlayer().getUser() + ": ");
    attackerUser.setStyle("-fx-font-size: 18px;");
    attackerBox.getChildren().add(attackerUser);

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
    Label defenderUser = new Label(defender.country.getPlayer().getUser() + ": ");
    defenderUser.setStyle("-fx-font-size: 18px;");
    defenderBox.getChildren().add(defenderUser);

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
    diceHBox.getChildren().addAll(attackerBox, defenderBox);

    PauseTransition delayTransition = new PauseTransition(Duration.millis(3000));
    delayTransition.setOnFinished(delayTransitionEvent -> {
      dicePopup.hide();
      if (attacker.getCountry().getTroops() > 1 && lastAttack.getHasConquered()
          && activePlayerUi.getPlayerUi().getPlayer()
          .equals(GameSceneController.getPlayerController().getPlayer())) {
        attacker.showAmountOfTroopsPopUp(
            lastAttack.getAttackingCountry().getTroops() - 1 - lastAttack.getTroopNumber(),
            defender, FORTIFY_PHASE);
      }
    });

    dicePane.setCenter(diceHBox);
    dicePane.setPrefSize(gamePane.getWidth() * 0.50, gamePane.getHeight() * 0.50);
    dicePane.setStyle("-fx-background-color: rgba(255, 255, 255, 0.3); -fx-background-radius: 10;");

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
    attacker.animateActiveCountry(activePlayerUi.getPlayerUi().getPlayerColor());
    defender.animateActiveCountry((Color) defender.getCountryPath().getFill());
    delayTransition.play();

  }

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
    arrow.setEndY(clickPosInGroup.getY());
    setCursor(Cursor.MOVE);

    Timeline timeline = new Timeline();

    KeyValue endXValue = new KeyValue(arrow.endXProperty(), clickPosInGroupToCountry.getX());
    KeyValue endYValue = new KeyValue(arrow.endYProperty(), clickPosInGroupToCountry.getY());
    KeyFrame keyFrame = new KeyFrame(Duration.millis(600), endXValue, endYValue);

    timeline.getKeyFrames().add(keyFrame);
    timeline.setCycleCount(1);
    timeline.setAutoReverse(false);
    timeline.play();
  }

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

  private void animateActiveCountry(Color playerColor) {
    Color brightHighlightColor = playerColor.deriveColor(0, 0, 0, 0.8);
    Color countryPathFill = (Color) this.countryPath.getFill();
    FillTransition highlightTransition = new FillTransition(Duration.seconds(0.2), this.countryPath,
        countryPathFill, brightHighlightColor);
    highlightTransition.setInterpolator(Interpolator.EASE_BOTH);
    FillTransition revertTransition = new FillTransition(Duration.seconds(0.2), this.countryPath,
        brightHighlightColor, countryPathFill);
    revertTransition.setInterpolator(Interpolator.EASE_BOTH);
    SequentialTransition sequentialTransition = new SequentialTransition(highlightTransition,
        revertTransition);
    sequentialTransition.play();
  }

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

  public boolean checkIfCountryIsMine(Country country) {
    if (country.getPlayer() == null) {
      return false;
    }
    return country.getPlayer().equals(GameSceneController.getPlayerController().getPlayer());
  }

  public SVGPath getCountryPath() {
    return countryPath;
  }

  public Country getCountry() {
    return country;
  }

  public void setTroopsCounterUi(TroopsCounterUi troopsCounterUi) {
    this.troopsCounterUi = troopsCounterUi;
  }

  public TroopsCounterUi getTroopsCounterUi() {
    return troopsCounterUi;
  }

  public DropShadow getGlowEffect() {
    return glowEffect;
  }

  public void setAdjacentCountryUis(Set<CountryUi> adjacentCountryUis) {
    this.adjacentCountryUis = adjacentCountryUis;
  }

  public void setCountry(Country country) {
    this.country = country;
  }

  public Set<CountryUi> getAdjacentCountryUis() {
    return adjacentCountryUis;
  }

  public void setColor(Color color) {
    this.color = color;
  }

  public void animateTutorialCountry() {
    fillTransition = new FillTransition(Duration.seconds(0.5));
    fillTransition.setShape(this.getCountryPath());
    fillTransition.setFromValue((Color) this.getCountryPath().getFill());
    fillTransition.setToValue(Color.rgb(128, 50, 189));
    fillTransition.setCycleCount(Animation.INDEFINITE);
    fillTransition.setAutoReverse(true);
    fillTransition.play();
  }
}

