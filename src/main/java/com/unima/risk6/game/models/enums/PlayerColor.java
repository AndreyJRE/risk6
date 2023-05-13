package com.unima.risk6.game.models.enums;

import javafx.scene.paint.Color;

/**
 * Enumeration of the possible colors that a player can be assigned in a game of Risk.
 *
 * @author wphung
 */
public enum PlayerColor {
  RED(Color.rgb(255, 102, 102)), BLUE(Color.rgb(102, 102, 255)), PURPLE(
      Color.rgb(178, 102, 255)), YELLOW(Color.rgb(255, 255, 102)), //YELLOW(Color.rgb(255, 255, 27))
  GREEN(Color.rgb(132, 255, 132)), ORANGE(Color.rgb(255, 178, 102));
  private final Color color;


  /**
   * Constructor for a player color.
   *
   * @param color the JavaFX color associated with this player color
   */
  PlayerColor(Color color) {
    this.color = color;
  }

  public Color getColor() {
    return color;
  }
}

