package com.unima.risk6.gui.scenes;

import com.unima.risk6.game.models.Country;
import javafx.scene.effect.Glow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;

public class CountryUI extends SVGPath {

  private Country country;

  private Glow glowEffect;

  public CountryUI(Country country, String SVGPath) {
    super();
    this.country = country;
    this.setContent(SVGPath);

    glowEffect = new Glow();
    glowEffect.setLevel(0.9);

    setOnMouseEntered((MouseEvent event) -> {
      setEffect(glowEffect);
    });

    setOnMouseExited((MouseEvent event) -> {
      setEffect(null);
    });
  }
}
