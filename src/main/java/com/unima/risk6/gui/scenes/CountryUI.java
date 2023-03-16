package com.unima.risk6.gui.scenes;

import com.unima.risk6.game.models.Country;
import javafx.scene.layout.Pane;

public class CountryUI extends Pane {

  private Country country;

  public CountryUI(Country country) {
    super();
    this.country = country;
  }
}
