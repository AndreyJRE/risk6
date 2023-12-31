package com.unima.risk6.gui.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

/**
 * Controller class for the splash screen. Implements the Initializable interface. Provides methods
 * to initialize the controller and access the progress label.
 *
 * @author mmeider
 */

public class SplashScreenController implements Initializable {

  @FXML
  private Label progress;

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
  }

  public Label getProgress() {
    return progress;
  }
}
