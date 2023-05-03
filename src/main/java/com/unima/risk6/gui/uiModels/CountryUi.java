package com.unima.risk6.gui.uiModels;

import com.unima.risk6.game.models.Country;
import com.unima.risk6.gui.controllers.GameSceneController;
import java.util.Set;
import javafx.animation.FillTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Bounds;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
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

  private int amountOfTroops = 0;

  private static boolean isCountrySelectedToAttackOthers = false;


  public CountryUi(Country country, String SVGPath) {
    super();
    this.country = country;
    this.countryPath = new SVGPath();
    this.troopsCounterUi = null;
    this.countryPath.setContent(SVGPath);
    this.countryPath.setFill(Color.WHITE);
    this.countryPath.setStroke(Color.BLACK);
    this.getChildren().add(new Group(this.countryPath));

    FillTransition highlightTransition = new FillTransition(Duration.seconds(0.1), countryPath,
        Color.WHITE, Color.RED);
    highlightTransition.setInterpolator(Interpolator.EASE_BOTH);
    FillTransition reverseTransition = new FillTransition(Duration.seconds(0.15), countryPath,
        Color.RED, Color.WHITE);
    reverseTransition.setInterpolator(Interpolator.EASE_BOTH);
    glowEffect = new DropShadow();
    glowEffect.setColor(Color.RED);

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
    setOnMouseEntered((MouseEvent event) -> {
      this.setCursor(Cursor.CROSSHAIR);
    });
    setOnMouseClicked(event -> {
      Group countriesGroup = (Group) this.getParent();
      //TODO Handle for the player who is playing
      // switch(GameSceneController.getMyPlayerUi().getPlayer().getCurrentPhase()) like this
      // animation for another users would be happening only when it game state is updated
      switch (GameSceneController.mockGamePhase) {
        case CLAIM_PHASE:
          if (this.countryPath.getFill() == Color.WHITE) {
            BorderPane currentRoot = (BorderPane) this.getParent().getParent().getParent();
            StackPane bottomPane = (StackPane) currentRoot.getBottom();
            ActivePlayerUi activePlayerUi = (ActivePlayerUi) bottomPane.lookup("#activePlayerUi");
            this.countryPath.setFill(activePlayerUi.getPlayerUi().getPlayerColor());
            GameSceneController.checkIfAllCountriesOccupied(countriesGroup);
          }
          break;
        case ATTACK_PHASE:
          animateAttackPhase(countriesGroup);
          break;
        // add more cases for other enum values
      }


    });
  }

  private SVGPath svgPathClone(SVGPath original) {
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
      CountryUi adjacentCountryUi) {
    adjacentCountryPath.setOnMouseClicked(event -> {
      BorderPane gamePane = (BorderPane) this.getParent().getParent().getParent();
      BorderPane chatBoxPane = new BorderPane();

      Label chatLabel = new Label("Amount of Troops: " + amountOfTroops);
      chatLabel.setStyle("-fx-font-size: 18px; -fx-background-color: white;");

      Button closeButton = new Button();
      closeButton.setPrefSize(15, 15);
      ImageView closeIcon = new ImageView(
          new Image(getClass().getResource("/com/unima/risk6/pictures/closeIcon.png").toString()));
      closeIcon.setFitWidth(15);
      closeIcon.setFitHeight(15);
      closeButton.setGraphic(closeIcon);
      closeButton.setStyle("-fx-background-radius: 15px;");
      closeButton.setFocusTraversable(false);

      chatBoxPane.setTop(closeButton);
      chatBoxPane.setAlignment(closeButton, Pos.TOP_RIGHT);

      HBox chatBox = new HBox();
      chatBox.setAlignment(Pos.CENTER);
      chatBox.setSpacing(15);

      Popup chatPopup = new Popup();
      closeButton.setOnAction(closeEvent -> chatPopup.hide());

      Circle leftCircle = new Circle(25);
      Image leftImage = new Image(
          getClass().getResource("/com/unima/risk6/pictures/minusIcon.png").toString());
      leftCircle.setFill(new ImagePattern(leftImage));
      leftCircle.setOnMouseClicked(minusEvent -> {
        if (amountOfTroops > 0) {
          amountOfTroops--;
          chatLabel.setText("Amount of Troops: " + amountOfTroops);
        }
      });

      Circle rightCircle = new Circle(25);
      Image rightImage = new Image(
          getClass().getResource("/com/unima/risk6/pictures/plusIcon.png").toString());
      rightCircle.setFill(new ImagePattern(rightImage));
      rightCircle.setOnMouseClicked(plusEvent -> {
        amountOfTroops++;
        chatLabel.setText("Amount of Troops: " + amountOfTroops);
      });

      Circle confirmCircle = new Circle(25);
      Image confirmImage = new Image(
          getClass().getResource("/com/unima/risk6/pictures/confirmIcon.png").toString());
      confirmCircle.setFill(new ImagePattern(confirmImage));
      confirmCircle.setOnMouseClicked(confirmEvent -> {
        chatPopup.hide();
        Group countriesGroup = (Group) this.getParent();
        countriesGroup.getChildren().removeIf(
            countriesGroupNode -> countriesGroupNode instanceof Line
                || countriesGroupNode instanceof SVGPath);
        setCursor(Cursor.DEFAULT);
        isCountrySelectedToAttackOthers = false;
        GameSceneController.getPlayerController()
            .sendAttack(this.country, adjacentCountryUi.getCountry(), amountOfTroops);
        //GameSceneController.animateFortify(animateFortify);
      });

      chatBox.getChildren().addAll(leftCircle, chatLabel, rightCircle, confirmCircle);
      chatBox.setHgrow(confirmCircle, Priority.ALWAYS);

      chatBoxPane.setCenter(chatBox);
      chatBoxPane.setPrefSize(gamePane.getWidth() * 0.40, gamePane.getHeight() * 0.20);
      chatBoxPane.setStyle("-fx-background-color: #F5F5F5; -fx-background-radius: 10;");
      DropShadow dropShadow = new DropShadow();
      dropShadow.setColor(Color.BLACK);
      dropShadow.setRadius(10);
      chatBoxPane.setEffect(dropShadow);

      Bounds rootBounds = gamePane.localToScreen(gamePane.getBoundsInLocal());

      double centerX = rootBounds.getMinX() + rootBounds.getWidth() / 2;
      double centerY = rootBounds.getMinY() + rootBounds.getHeight() / 2;

      double popupWidth = chatBoxPane.getPrefWidth();
      double popupHeight = chatBoxPane.getPrefHeight();

      chatPopup.getContent().add(chatBoxPane);

      chatPopup.setX(centerX - popupWidth / 2);
      chatPopup.setY(centerY - popupHeight / 2);
      chatPopup.show(gamePane.getScene().getWindow());


    });
    adjacentCountryPath.setOnMouseEntered(event -> {
      adjacentCountryPath.setCursor(Cursor.HAND);
    });
  }

  public void animateAttackPhase(Group countriesGroup) {
    if (isCountrySelectedToAttackOthers) {
      countriesGroup.getChildren().removeIf(countriesGroupNode -> countriesGroupNode instanceof Line
          || countriesGroupNode instanceof SVGPath);
      setCursor(Cursor.DEFAULT);
      isCountrySelectedToAttackOthers = false;

    } else {
      for (CountryUi adjacentCountryUi : adjacentCountryUis) {
        Line arrow = createArrowAndAnimateAdjacentCountries(countriesGroup, adjacentCountryUi);
        animateArrow(adjacentCountryUi, arrow);
      }
      isCountrySelectedToAttackOthers = true;
    }
  }

  private Line createArrowAndAnimateAdjacentCountries(Group countriesGroup,
      CountryUi adjacentCountryUi) {
    SVGPath adjacentCountryPath = svgPathClone(adjacentCountryUi.getCountryPath());
    adjacentCountryPath.setEffect(adjacentCountryUi.getGlowEffect());
    addEventHandlersToAdjacentCountryPath(adjacentCountryPath, adjacentCountryUi);
    Line arrow = new Line();
    arrow.setStroke(Color.RED);
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

