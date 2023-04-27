package com.unima.risk6.gui.uiModels;

import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.enums.CountryName;
import java.util.Set;
import javafx.animation.FillTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

public class CountryUi extends Group {

  private final Country country;

  private Set<CountryUi> adjacentCountryUis;

  private final CountryName countryId;

  private final SVGPath countryPath;

  private final DropShadow glowEffect;

  private TroopsCounterUi troopsCounterUi;
  private static boolean isCountrySelectedToAttackOthers = false;


  public CountryUi(Country country, String SVGPath) {
    super();
    this.country = country;
    this.countryId = country.getCountryName();
    this.countryPath = new SVGPath();
    this.troopsCounterUi = null;
    this.countryPath.setContent(SVGPath);
    this.countryPath.setFill(Color.WHITE);
    this.countryPath.setStroke(Color.BLACK);

//    Bounds bounds = this.countryPath.getBoundsInParent();

//    this.ellipseX = bounds.getMinX() + bounds.getWidth() * 0.5;
//    this.ellipseY = bounds.getMinY() + bounds.getHeight() * 0.5;

//    this.correctEllipsePlacement(this.country, this.ellipseX, this.ellipseY);

    this.getChildren().add(new Group(this.countryPath));

    FillTransition highlightTransition = new FillTransition(Duration.seconds(0.1), countryPath,
        Color.WHITE, Color.RED);
    highlightTransition.setInterpolator(Interpolator.EASE_BOTH);
    FillTransition reverseTransition = new FillTransition(Duration.seconds(0.15), countryPath,
        Color.RED, Color.WHITE);
    reverseTransition.setInterpolator(Interpolator.EASE_BOTH);

    glowEffect = new DropShadow();
    glowEffect.setColor(Color.RED);
    initMouseListener();
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
      if (isCountrySelectedToAttackOthers) {
        countriesGroup.getChildren()
            .removeIf(countriesGroupNode -> countriesGroupNode instanceof Line
                || countriesGroupNode instanceof SVGPath);
        setCursor(Cursor.DEFAULT);
        isCountrySelectedToAttackOthers = false;
      } else {
        System.out.println("Country clicked: " + this.getCountryId());
        for (CountryUi adjacentCountryUi : adjacentCountryUis) {
          System.out.println(adjacentCountryUi.getCountryId());
          SVGPath countryPath1 = svgPathClone(adjacentCountryUi.getCountryPath());
          countryPath1.setEffect(adjacentCountryUi.getGlowEffect());
          Line arrow = new Line();
          arrow.setStroke(Color.RED);
          int index = 0;
          for (Node troopsCounterUiNode : countriesGroup.getChildren()) {
            if (troopsCounterUiNode instanceof TroopsCounterUi) {
              countriesGroup.getChildren().add(index++, countryPath1);
              break;
            } else {
              index++;
            }
          }
          index = 0;
          for (Node troopsCounterUiNode : countriesGroup.getChildren()) {
            if (troopsCounterUiNode instanceof TroopsCounterUi) {
              countriesGroup.getChildren().add(index++, arrow);
              break;
            } else {
              index++;
            }
          }
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
        // arrow.setVisible(true);
        isCountrySelectedToAttackOthers = true;
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

  public SVGPath getCountryPath() {
    return countryPath;
  }

  public Country getCountry() {
    return country;
  }

  public CountryName getCountryId() {
    return countryId;
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
}

