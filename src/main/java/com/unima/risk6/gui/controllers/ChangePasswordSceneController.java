package com.unima.risk6.gui.controllers;

import static com.unima.risk6.gui.configurations.StyleConfiguration.applyButtonStyle;

import com.unima.risk6.gui.configurations.SceneConfiguration;
import com.unima.risk6.gui.configurations.StyleConfiguration;
import com.unima.risk6.gui.controllers.enums.SceneName;
import com.unima.risk6.gui.scenes.ChangePasswordScene;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;

public class ChangePasswordSceneController {

  private final ChangePasswordScene changePasswordScene;

  private final SceneController sceneController;

  private BorderPane root;

  public ChangePasswordSceneController(ChangePasswordScene changePasswordScene) {
    this.changePasswordScene = changePasswordScene;
    this.sceneController = SceneConfiguration.getSceneController();
  }

  public void init() {
    root = (BorderPane) changePasswordScene.getRoot();
    root.setPrefWidth(SceneConfiguration.getWidth());
    root.setPrefHeight(SceneConfiguration.getHeight());
    Path arrow = StyleConfiguration.generateBackArrow();
    StackPane backButton = new StackPane(arrow);
    backButton.setOnMouseClicked(event -> sceneController.activate(SceneName.USER_OPTION));
    VBox vBox = createNewPasswordFields();
    vBox.setAlignment(Pos.CENTER);
    vBox.setSpacing(25);

    // Add some spacing around backButton
    BorderPane.setMargin(backButton, new Insets(10, 0, 0, 10));
    root.setCenter(vBox);
    root.setLeft(backButton);


  }

  private Button createConfirmButton() {
    Button confirmButton = new Button("Confirm");
    confirmButton.setPrefWidth(470);
    confirmButton.setPrefHeight(40);
    confirmButton.setAlignment(Pos.CENTER);
    applyButtonStyle(confirmButton);
    confirmButton.setFont(new Font(18));
    return confirmButton;
  }

  private VBox createNewPasswordFields() {
    VBox vBox = new VBox();
    Label selectUser = new Label("Change password");
    selectUser.setStyle("-fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 65px; "
        + "-fx-font-weight: bold; -fx-text-fill: #2D2D2D;");
    PasswordField newPasswordField = new PasswordField();
    PasswordField confirmPasswordField = new PasswordField();
    newPasswordField.setPromptText("New password");
    newPasswordField.setStyle(
        "-fx-font-size: 20; -fx-background-radius: 20; -fx-border-radius: 20;");
    newPasswordField.setPrefWidth(470);
    confirmPasswordField.setPromptText("Confirm password");
    confirmPasswordField.setStyle(
        "-fx-font-size: 20; -fx-background-radius: 20; -fx-border-radius: 20;");
    confirmPasswordField.setPrefWidth(470);
    Button confirmButton = createConfirmButton();
    confirmButton.setOnAction(event -> {
      //TODO implement password change in database
      sceneController.activate(SceneName.USER_OPTION);
    });
    HBox newPasswordBox = new HBox(newPasswordField);
    newPasswordBox.setAlignment(Pos.CENTER);
    HBox confirmPasswordBox = new HBox(confirmPasswordField);
    confirmPasswordBox.setAlignment(Pos.CENTER);
    vBox.getChildren().addAll(selectUser, newPasswordBox, confirmPasswordBox, confirmButton);
    return vBox;
  }

}
