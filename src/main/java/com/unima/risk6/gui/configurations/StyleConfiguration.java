package com.unima.risk6.gui.configurations;

import javafx.scene.control.Button;

public class StyleConfiguration {

  private static final String NORMAL_BUTTON_STYLE = "-fx-background-color: linear-gradient"
      + "(#FFDAB9, #FFA07A)"
      + "; -fx-background-radius: 40; -fx-border-radius: 40"
      + "; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.14), 10, 0, 0, 0);"
      + " -fx-text-fill: #FFFFFF;";
  private static final String HOVER_BUTTON_STYLE = "-fx-background-color: linear-gradient"
      + "(#FFA07A, "
      + "#FFDAB9)"
      + "; -fx-background-radius: 40; -fx-border-radius: 40"
      + "; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 0);"
      + " -fx-text-fill: #FFFFFF;";

  public static void applyButtonStyle(Button button) {
    button.setStyle(NORMAL_BUTTON_STYLE);
    button.hoverProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue) {
        button.setStyle(HOVER_BUTTON_STYLE);
      } else {
        button.setStyle(NORMAL_BUTTON_STYLE);
      }
    });
  }
}
