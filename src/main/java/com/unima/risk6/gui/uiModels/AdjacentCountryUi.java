package com.unima.risk6.gui.uiModels;

import com.unima.risk6.game.models.Country;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.SVGPath;


public class AdjacentCountryUi extends SVGPath {

  private Line line;

  private Country country;

  public AdjacentCountryUi(SVGPath svgPath, Country country) {
    this.setContent(svgPath.getContent());
    this.setFill(Color.WHITE);
    this.setStroke(Color.BLACK);
    this.country = country;
  }

  public void setLine(Line line) {
    this.line = line;
  }

  public Line getLine() {
    return line;
  }

  public Country getCountry() {
    return country;
  }
}
