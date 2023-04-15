package com.unima.risk6.gui.controllers;

import com.unima.risk6.RisikoMain;
import com.unima.risk6.gui.configurations.SceneConfiguration;
import com.unima.risk6.gui.controllers.enums.SceneName;
import java.io.IOException;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.util.Duration;

public class SecurityQuestionsController {

  @FXML
  private ComboBox<String> FirstSecurityQuestionComboBox;

  @FXML
  private TextField firstAnswerField;

  @FXML
  private ComboBox<String> SecondSecurityQuestionComboBox;

  @FXML
  private TextField secondAnswerField;

  @FXML
  private ComboBox<String> ThirdSecurityQuestionComboBox;

  @FXML
  private TextField thirdAnswerField;

  @FXML
  private Button saveButton;
  @FXML
  private void initialize() {
    // Add items to the ComboBox
    FirstSecurityQuestionComboBox.getItems().addAll(
        "What is your mother's maiden name?",
        "In what city were you born?",
        "What year was your father (or mother) born?",
        "What was your favorite food as a child?",
        "In what city or town was your first job?"
    );

    SecondSecurityQuestionComboBox.getItems().addAll(
        "What is your mother's maiden name?",
        "In what city were you born?",
        "What year was your father (or mother) born?",
        "What was your favorite food as a child?",
        "In what city or town was your first job?"
    );

    ThirdSecurityQuestionComboBox.getItems().addAll(
        "What is your mother's maiden name?",
        "In what city were you born?",
        "What year was your father (or mother) born?",
        "What was your favorite food as a child?",
        "In what city or town was your first job?"
    );

    // Register callbacks for ComboBox objects
    FirstSecurityQuestionComboBox.setOnAction(event -> {
      SecondSecurityQuestionComboBox.getItems().remove(
          FirstSecurityQuestionComboBox.getValue());
      ThirdSecurityQuestionComboBox.getItems().remove(
          FirstSecurityQuestionComboBox.getValue());
    });

    SecondSecurityQuestionComboBox.setOnAction(event -> {
      FirstSecurityQuestionComboBox.getItems().remove(
          SecondSecurityQuestionComboBox.getValue());
      ThirdSecurityQuestionComboBox.getItems().remove(
          SecondSecurityQuestionComboBox.getValue());
    });

    ThirdSecurityQuestionComboBox.setOnAction(event -> {
      FirstSecurityQuestionComboBox.getItems().remove(
          ThirdSecurityQuestionComboBox.getValue());
      SecondSecurityQuestionComboBox.getItems().remove(
          ThirdSecurityQuestionComboBox.getValue());
    });
  }
  @FXML
  private void handSaveButton() {

    saveButton.setStyle(
        "-fx-background-color: linear-gradient(#FFA07A, #FFDAB9); -fx-text-fill: #FFFFFF;  "
            + "-fx-background-radius: 20; -fx-border-radius: 20; "
            + "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.14), 10, 0, 0, 0);");
    // Change it back after 200 milliseconds
    PauseTransition pause = new PauseTransition(Duration.millis(200));
    pause.setOnFinished(e -> saveButton.setStyle("-fx-background-color: linear-gradient(#FFDAB9, "
        + "#FFA07A); -fx-text-fill: #FFFFFF; -fx-background-radius: 20; "
        + "-fx-border-radius: 20; "
        + "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.14), 10, 0, 0, 0);"));
    pause.play();

    //TODO: Implement Database connection

    //Go to Title Screen
    activateTitleScreen();
  }

  static void activateTitleScreen() {
    FXMLLoader fxmlLoader = new FXMLLoader(RisikoMain.class.getResource("fxml/TitleScreen.fxml"));
    Scene scene = null;
    try {
      scene = new Scene(fxmlLoader.load());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    SceneController sceneController = SceneConfiguration.getSceneController();
    sceneController.addScene(SceneName.TITLE, scene);
    sceneController.activate(SceneName.TITLE);
  }
}
