package com.unima.risk6.game.models.enums;

import javafx.scene.paint.Color;

/**
 * Enumeration of the possible colors that a player can be assigned in a game of Risk.
 *
 * @author wphung
 */
public enum PlayerColor {
  RED(Color.RED), BLUE(Color.BLUE), PURPLE(Color.PURPLE), YELLOW(Color.YELLOW),
  GREEN(Color.GREEN), ORANGE(Color.ORANGE);
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

