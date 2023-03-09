package com.unima.risk6.gui.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class SplashScreenController implements Initializable {

  @FXML
  private Label progress;


  @FXML
  private void handlebuttonAction(ActionEvent event) {

  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
  }

  public Label getProgress() {
    return progress;
  }
}
