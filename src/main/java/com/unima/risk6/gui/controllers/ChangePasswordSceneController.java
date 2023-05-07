package com.unima.risk6.gui.controllers;

import static com.unima.risk6.gui.configurations.StyleConfiguration.applyButtonStyle;

import com.unima.risk6.database.configurations.DatabaseConfiguration;
import com.unima.risk6.database.exceptions.NotValidPasswordException;
import com.unima.risk6.database.models.User;
import com.unima.risk6.database.services.UserService;
import com.unima.risk6.gui.configurations.SceneConfiguration;
import com.unima.risk6.gui.configurations.SessionManager;
import com.unima.risk6.gui.configurations.StyleConfiguration;
import com.unima.risk6.gui.controllers.enums.SceneName;
import com.unima.risk6.gui.scenes.ChangePasswordScene;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;

public class ChangePasswordSceneController {

  private final ChangePasswordScene changePasswordScene;

  private final SceneController sceneController;

  private BorderPane root;

  private final UserService userService;

  public ChangePasswordSceneController(ChangePasswordScene changePasswordScene) {
    this.changePasswordScene = changePasswordScene;
    this.userService = DatabaseConfiguration.getUserService();
    this.sceneController = SceneConfiguration.getSceneController();
  }

  public void init() {
    root = (BorderPane) changePasswordScene.getRoot();
    root.setPrefWidth(SceneConfiguration.getWidth());
    root.setPrefHeight(SceneConfiguration.getHeight());
    Path arrow = StyleConfiguration.generateBackArrow();
    StackPane backButton = new StackPane(arrow);
    backButton.setOnMouseClicked(event -> sceneController.activate(SceneName.USER_OPTION));
    AnchorPane anchorPane = createNewPasswordFields();

    // Add some spacing around backButton
    BorderPane.setMargin(backButton, new Insets(10, 0, 0, 10));
    root.setCenter(anchorPane);
    root.setLeft(backButton);


  }

  private Button createConfirmButton() {
    Button confirmButton = new Button("Confirm");
    confirmButton.setPrefWidth(800);
    confirmButton.setPrefHeight(40);
    confirmButton.setAlignment(Pos.CENTER);
    applyButtonStyle(confirmButton);
    confirmButton.setFont(new Font(18));
    return confirmButton;
  }

  private AnchorPane createNewPasswordFields() {
    AnchorPane anchorPane = new AnchorPane();
    anchorPane.setPrefSize(600, 495);
    anchorPane.setPadding(new Insets(150, 200, 150, 200));

    VBox vBox = new VBox();
    vBox.setAlignment(Pos.CENTER);
    vBox.setSpacing(22);
    vBox.setStyle(
        "-fx-background-color: #FFFFFF; -fx-background-radius: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.14), 10, 0, 0, 0);");
    AnchorPane.setTopAnchor(vBox, 0.0);
    AnchorPane.setRightAnchor(vBox, 0.0);
    AnchorPane.setBottomAnchor(vBox, 0.0);
    AnchorPane.setLeftAnchor(vBox, 0.0);

    Label selectUser = new Label("Change Password");
    selectUser.setStyle("-fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 41px; "
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

    Label errorMessage = new Label();
    errorMessage.setTextFill(Color.RED);
    errorMessage.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");
    Button confirmButton = createConfirmButton();
    confirmButton.setDisable(true); // Disable the button initially

    ChangeListener<String> passwordFieldChangeListener = (observable, oldValue, newValue) -> {
      if (newPasswordField.getText().equals(confirmPasswordField.getText())) {
        // Passwords match, enable the submit button and clear the error message
        confirmButton.setDisable(false);
        errorMessage.setText("");
      } else {
        // Passwords don't match, disable the submit button and display an error message
        confirmButton.setDisable(true);
        errorMessage.setText("Passwords do not match.");
      }
    };
    confirmButton.setOnAction(event -> {
      //TODO implement password change in database
      User user = SessionManager.getUser();
      user.setPassword(newPasswordField.getText());
      try {
        userService.updateUser(user);
        sceneController.activate(SceneName.USER_OPTION);
      } catch (IllegalArgumentException illegalArgumentException) {
        showErrorDialog("Error", "Password should be non-empty!");
      } catch (NotValidPasswordException validationException) {
        showErrorDialog("Error", validationException.getMessage());
      }
    });
    newPasswordField.textProperty().addListener(passwordFieldChangeListener);
    confirmPasswordField.textProperty().addListener(passwordFieldChangeListener);
    vBox.setAlignment(Pos.CENTER);

    Label enterNewPassword = new Label("Enter new password:");
    Label confirmPassword = new Label("Confirm new password:");
    enterNewPassword.setFont(Font.font(18));
    confirmPassword.setFont(Font.font(18));
    enterNewPassword.setAlignment(Pos.CENTER_LEFT);
    confirmPassword.setAlignment(Pos.CENTER_LEFT);
    enterNewPassword.setMinWidth(200);
    confirmPassword.setMinWidth(200);

    GridPane centerGrid = new GridPane();
    centerGrid.add(enterNewPassword, 0, 0);
    centerGrid.add(confirmPassword, 0, 1);
    centerGrid.add(newPasswordField, 1, 0);
    centerGrid.add(confirmPasswordField, 1, 1);
    centerGrid.setHgap(20);
    centerGrid.setVgap(15);

    vBox.getChildren().addAll(selectUser, centerGrid, errorMessage, confirmButton);
    vBox.setPadding(new Insets(15, 20, 15, 20));

    anchorPane.getChildren().add(vBox);
    return anchorPane;
  }

  private void showErrorDialog(String title, String message) {
    Alert alert = new Alert(AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }

}
