package com.unima.risk6.gui.scenes;

import com.unima.risk6.game.models.Country;
import javafx.animation.FillTransition;
import javafx.animation.Interpolator;
import javafx.scene.effect.Glow;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

public class CountryUi extends SVGPath {

  private Country country;

  private Glow glowEffect;


  public CountryUi(Country country, String SVGPath) {
    super();
    this.country = country;
    this.setContent(SVGPath);
    this.setFill(Color.WHITE);
    this.setStroke(Color.BLACK);
    FillTransition highlightTransition = new FillTransition(Duration.seconds(0.1), this,
        Color.WHITE, Color.RED);
    highlightTransition.setInterpolator(Interpolator.EASE_BOTH);
    FillTransition reverseTransition = new FillTransition(Duration.seconds(0.15), this, Color.RED,
        Color.WHITE);
    reverseTransition.setInterpolator(Interpolator.EASE_BOTH);
    glowEffect = new Glow();
    glowEffect.setLevel(0.9);

    setOnMouseEntered((MouseEvent event) -> {
      setEffect(glowEffect);
      highlightTransition.play();
    });

    setOnMouseExited((MouseEvent event) -> {
      setEffect(null);
      reverseTransition.play();
    });

    setOnMouseClicked((MouseEvent event) -> {
      System.out.println("Clicked on " + country.getCountryName());
    });
  }
}
