package com.unima.risk6.gui.models.enums;

import javafx.scene.paint.Color;

public enum PlayerColor {
  RED(Color.RED), BLUE(Color.BLUE), PURPLE(Color.PURPLE), YELLOW(Color.YELLOW),
  GREEN(Color.GREEN), ORANGE(Color.ORANGE);
  private Color color;

  PlayerColor(Color color) {
    this.color = color;
  }
}

