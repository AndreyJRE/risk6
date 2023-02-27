module com.unima.risk6 {
  requires javafx.controls;
  requires javafx.fxml;

  opens com.unima.risk6 to javafx.fxml;
  exports com.unima.risk6;
  exports com.unima.risk6.gui.controllers;
  opens com.unima.risk6.gui.controllers to javafx.fxml;
//  exports com.unima.risk6.game;
//  opens com.unima.risk6.game to javafx.fxml;
  exports com.unima.risk6.game.models;
  opens com.unima.risk6.game.models to javafx.fxml;
  exports com.unima.risk6.game.logic;
  opens com.unima.risk6.game.logic to javafx.fxml;
}