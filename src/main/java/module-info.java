module com.unima.risk6 {
  requires javafx.controls;
  requires javafx.fxml;

  opens com.unima.risk6 to javafx.fxml;
  exports com.unima.risk6;
  exports com.unima.risk6.gui.controllers;
  opens com.unima.risk6.gui.controllers to javafx.fxml;
  exports com.unima.risk6.gui.models;
  opens com.unima.risk6.gui.models to javafx.fxml;
}