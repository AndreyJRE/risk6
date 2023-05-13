package com.unima.risk6.gui.controllers.enums;

import javafx.scene.paint.Color;

/**
 * Enumeration of the possible colors  can be assigned in a game of Risk.
 *
 * @author jferch
 */
public enum Colors {
  COUNTRY_BACKGROUND(Color.rgb(249, 236, 195)), COUNTRY_BACKGROUND_DARKEN(
      Color.rgb(243, 217, 134)), //COUNTRY_STROKE(Color.rgb(254, 251, 244)),
  COUNTRY_STROKE(Color.rgb(0, 7, 45)),
  WATER(Color.rgb(149, 115, 97)),
  TEXT(Color.rgb(0, 7, 45));
  private final Color color;


  /**
   * Constructor for a player color.
   *
   * @param color the JavaFX color associated with this player color
   */
  Colors(Color color) {
    this.color = color;
  }

  public Color getColor() {
    return color;
  }
}


