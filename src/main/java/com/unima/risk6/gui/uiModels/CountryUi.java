package com.unima.risk6.gui.uiModels;

import static com.unima.risk6.game.models.enums.GamePhase.ATTACK_PHASE;
import static com.unima.risk6.game.models.enums.GamePhase.FORTIFY_PHASE;
import static com.unima.risk6.game.models.enums.GamePhase.REINFORCEMENT_PHASE;

import com.unima.risk6.game.configurations.GameConfiguration;
import com.unima.risk6.game.logic.Attack;
import com.unima.risk6.game.logic.controllers.PlayerController;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.enums.GamePhase;
import com.unima.risk6.gui.controllers.GameSceneController;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.animation.FillTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
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


  public CountryUi(Country country, String SVGPath) {
    super();
    this.country = country;
    this.countryPath = new SVGPath();
    this.troopsCounterUi = null;
    this.countryPath.setContent(SVGPath);
    this.countryPath.setFill(Color.WHITE);
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
              this.setCursor(Cursor.CROSSHAIR);
            }
          }
          case ATTACK_PHASE, FORTIFY_PHASE -> {
            if (checkIfCountryIsMine(country) && country.getTroops() > 1) {
              this.setCursor(Cursor.CROSSHAIR);
            }
          }
          case REINFORCEMENT_PHASE -> {
            if (checkIfCountryIsMine(country)
                && playerController.getPlayer().getDeployableTroops() > 0) {
              this.setCursor(Cursor.CROSSHAIR);
            }

          }
        }
      } else {
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
            playerController.sendReinforce(this.country, 1);
            playerController.sendEndPhase(currentPhase);
          }
        }
        case ATTACK_PHASE -> {
          if (checkIfCountryIsMine(country) && country.getTroops() > 1) {
            animateAttackPhase(countriesGroup);
          }
        }
        case FORTIFY_PHASE -> {
          if (checkIfCountryIsMine(country) && country.getTroops() > 1) {
            animateFortifyPhase(countriesGroup);
          }
        }
        case REINFORCEMENT_PHASE -> {
          if (checkIfCountryIsMine(country)
              && playerController.getPlayer().getDeployableTroops() > 0) {
            animateReinforcementPhase();
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
    chatLabel.setStyle("-fx-font-size: 18px; -fx-background-color: white;");

    HBox chatBox = new HBox();
    chatBox.setAlignment(Pos.CENTER);
    chatBox.setSpacing(15);

    Popup popUp = new Popup();

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
      //TODO fortify or attack depending
      popUp.hide();
      PlayerController playerController = GameSceneController.getPlayerController();
      if (gamePhase == FORTIFY_PHASE) {
        playerController.sendFortify(this.country, adjacentCountryUi.getCountry(),
            amountOfTroops.get());
        playerController.sendEndPhase(gamePhase);
        removeArrowsAndAdjacentCountries();
      } else if (gamePhase == ATTACK_PHASE) {
        playerController.sendAttack(this.country, adjacentCountryUi.getCountry(),
            amountOfTroops.get());
        removeArrowsAndAdjacentCountries();
      } else if (gamePhase == REINFORCEMENT_PHASE) {
        playerController.sendReinforce(this.country, amountOfTroops.get());
      }

    });

    chatBox.getChildren().addAll(leftCircle, chatLabel, rightCircle, confirmCircle);
    HBox.setHgrow(confirmCircle, Priority.ALWAYS);

    moveTroopsPane.setCenter(chatBox);
    moveTroopsPane.setPrefSize(gamePane.getWidth() * 0.40, gamePane.getHeight() * 0.20);
    moveTroopsPane.setStyle("-fx-background-color: #F5F5F5; -fx-background-radius: 10;");
    DropShadow dropShadow = new DropShadow();
    dropShadow.setColor(Color.BLACK);
    dropShadow.setRadius(10);
    moveTroopsPane.setEffect(dropShadow);

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

  private void removeArrowsAndAdjacentCountries() {
    Group countriesGroup = (Group) this.getParent();
    countriesGroup.getChildren().removeIf(countriesGroupNode -> countriesGroupNode instanceof Line
        || countriesGroupNode instanceof SVGPath);
    isCountrySelected = false;
  }

  private void showAttackDicePopUp(Attack lastAttack) {
    BorderPane gamePane = (BorderPane) this.getParent().getParent().getParent();
    BorderPane dicePane = new BorderPane();

    Label winningChanceLabel = new Label("Winning Chance:" + 1);
    winningChanceLabel.setStyle("-fx-font-size: 18px; -fx-background-color: white;");

    HBox diceHBox = new HBox();
    diceHBox.setAlignment(Pos.CENTER);
    diceHBox.setSpacing(20);
    Popup dicePopup = new Popup();

    //MOCKUP OF ATTACK DIALOG WITH DICE

    List<DiceUi> diceUis = new ArrayList<>();
    VBox attackerBox = new VBox();

    for (int i = 0; i < lastAttack.getAttackDiceResult().size(); i++) {
      DiceUi dice = new DiceUi(true, lastAttack.getAttackDiceResult().get(i));
      attackerBox.getChildren().add(dice);
      diceUis.add(dice);
    }
    attackerBox.setAlignment(Pos.CENTER);
    VBox defenderBox = new VBox();
    for (int i = 0; i < lastAttack.getDefendDiceResult().size(); i++) {
      DiceUi dice = new DiceUi(false, lastAttack.getDefendDiceResult().get(i));
      defenderBox.getChildren().add(dice);
      diceUis.add(dice);
    }
    defenderBox.setAlignment(Pos.CENTER);
    diceHBox.getChildren().addAll(attackerBox, winningChanceLabel, defenderBox);

    PauseTransition delayTransition = new PauseTransition(Duration.millis(3000));
    delayTransition.setOnFinished(delayTransitionEvent -> dicePopup.hide());

    dicePane.setCenter(diceHBox);
    dicePane.setPrefSize(gamePane.getWidth() * 0.50, gamePane.getHeight() * 0.50);
    dicePane.setStyle("-fx-background-color: #F5F5F5; -fx-background-radius: 10;");
    DropShadow dropShadow = new DropShadow();
    dropShadow.setColor(Color.BLACK);
    dropShadow.setRadius(10);
    dicePane.setEffect(dropShadow);

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
    FillTransition highlightTransition = new FillTransition(Duration.seconds(1), this.countryPath,
        (Color) this.countryPath.getFill(), playerColor);
    highlightTransition.setInterpolator(Interpolator.EASE_BOTH);
    glowEffect.setColor(playerColor);
    highlightTransition.play();
  }

  public void updateAfterAttack(ActivePlayerUi activePlayerUi, Attack attack, CountryUi attacker,
      CountryUi defender) {
    showAttackDicePopUp(attack);
    if (attack.getHasConquered()) {
      Color playerColor = activePlayerUi.getPlayerUi().getPlayerColor();
      FillTransition highlightTransition = new FillTransition(Duration.seconds(1),
          attacker.getCountryPath(), (Color) attacker.getCountryPath().getFill(), playerColor);
      highlightTransition.setInterpolator(Interpolator.EASE_BOTH);
      highlightTransition.play();
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

}

