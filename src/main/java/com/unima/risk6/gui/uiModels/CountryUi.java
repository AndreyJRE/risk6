package com.unima.risk6.gui.uiModels;

import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.enums.CountryName;
import javafx.animation.FillTransition;
import javafx.animation.Interpolator;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.effect.Glow;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.scene.shape.Line;

public class CountryUi extends Group {

  private Country country;

  private final CountryName countryId;

  private SVGPath countryPath;

  private Glow glowEffect;

  private TroopsCounterUi troopsCounterUi;


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
        Color.RED,
        Color.WHITE);
    reverseTransition.setInterpolator(Interpolator.EASE_BOTH);
    glowEffect = new Glow();
    glowEffect.setLevel(0.9);

    setOnMouseEntered((MouseEvent event) -> {
      this.setCursor(Cursor.CROSSHAIR);
    });

    //  setOnMouseExited((MouseEvent event) -> {
    //    setEffect(null);
    //    reverseTransition.play();
    //  });

    //  setOnMouseClicked((MouseEvent event) -> {
    //    System.out.println("Clicked on " + country.getCountryName());
    //  });
  }

  public static Point2D correctEllipsePlacement(Country country, double ellipseX, double ellipseY) {
    String countryName = country.getCountryName().name();

    switch (countryName) {
      case "ARGENTINA":
        ellipseX -= 15;
        break;
      case "BRAZIL":
        ellipseX += 20;
        break;
      case "PERU":
        ellipseX -= 10;
        ellipseY += 2;
        break;
      case "VENEZUELA":
        ellipseY -= 5;
        ellipseX -= 5;
        break;
      case "NORTH_WEST_TERRITORY":
        ellipseY += 15;
        break;
      case "ALASKA":
        ellipseY -= 10;
        break;
      case "URAL":
        ellipseX -= 10;
        break;
      case "KAMCHATKA":
        ellipseY -= 25;
        break;
      case "SIBERIA":
        ellipseX += 10;
        break;
      case "JAPAN":
        ellipseX += 5;
        break;
      case "EAST_AFRICA":
        ellipseX += 5;
        break;
      default:
        break;
    }
    return new Point2D(ellipseX, ellipseY);
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
}

