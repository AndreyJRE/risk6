package com.unima.risk6.gui.controllers;


import static com.unima.risk6.gui.configurations.StyleConfiguration.applyButtonStyle;
import static com.unima.risk6.gui.configurations.StyleConfiguration.showErrorDialog;

import com.unima.risk6.database.configurations.DatabaseConfiguration;
import com.unima.risk6.database.exceptions.NotValidPasswordException;
import com.unima.risk6.database.exceptions.UsernameNotUniqueException;
import com.unima.risk6.database.models.User;
import com.unima.risk6.database.services.UserService;
import com.unima.risk6.gui.configurations.SceneConfiguration;
import com.unima.risk6.gui.configurations.SessionManager;
import com.unima.risk6.gui.controllers.enums.SceneName;
import com.unima.risk6.gui.scenes.SelectedUserScene;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class CreateAccountController implements Initializable {

  private UserService userService;
  @FXML
  private VBox centerVBox;
  @FXML
  private AnchorPane root;
  @FXML
  private TextField usernameField;

  @FXML
  private PasswordField passwordField;

  @FXML
  private Button createButton;

  @FXML
  private PasswordField checkPasswordField;

  @FXML
  private Label passwordMismatchLabel;
  @FXML
  private ImageView backgroundImageView;

  private SceneController sceneController;

  @FXML
  private void handleLoginToAccount() {
    if(userService.getAllUsers().size() == 0) {
      showErrorDialog("No existing User", "There is no user in the database you can log into. Please create a User first!");
    }else{
      sceneController = SceneConfiguration.getSceneController();
      sceneController.activate(SceneName.LOGIN);
    }
  }

  @FXML
  private void handleCreateButton() {
    String username = usernameField.getText();
    String password = passwordField.getText();

    User user = new User(username, password, "/com/unima/risk6/pictures/playerIcon.png");
    try {
      userService.saveUser(user);
      sceneController = SceneConfiguration.getSceneController();
      SelectedUserScene scene = (SelectedUserScene) SceneConfiguration.getSceneController()
          .getSceneBySceneName(SceneName.SELECTED_USER);
      if (scene == null) {
        scene = new SelectedUserScene();
        SelectedUserSceneController selectedUserSceneController = new SelectedUserSceneController(
            scene);
        scene.setController(selectedUserSceneController);
        sceneController.addScene(SceneName.SELECTED_USER, scene);
      }
      SessionManager.setUser(user);

      sceneController.activate(SceneName.SELECTED_USER);
    } catch (IllegalArgumentException illegalArgumentException) {
      showErrorDialog("Error", "Password and username should be non-empty!");
      passwordField.setText("");
      checkPasswordField.setText("");
    } catch (UsernameNotUniqueException | NotValidPasswordException validationException) {
      showErrorDialog("Error", validationException.getMessage());
      passwordField.setText("");
      checkPasswordField.setText("");
    }
  }

  @FXML
  private void handleMouseEntered(MouseEvent event) {
    Label label = (Label) event.getSource();
    label.setStyle("-fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 14px; -fx-text-fill: "
        + "#007FFF; -fx-underline: true; -fx-cursor: hand");
  }

  @FXML
  private void handleMouseExited(MouseEvent event) {
    Label label = (Label) event.getSource();
    label.setStyle(
        "-fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 14px; -fx-text-fill: #BEBEBE; "
            + "-fx-underline: true;");
  }


  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    backgroundImageView.fitWidthProperty().bind(root.widthProperty());
    backgroundImageView.fitHeightProperty().bind(root.heightProperty());
    applyButtonStyle(createButton);
    userService = DatabaseConfiguration.getUserService();
    checkPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
      if (!newValue.equals(passwordField.getText())) {
        passwordMismatchLabel.setText("Passwords do not match!");
      } else {
        passwordMismatchLabel.setText("");
      }
    });
    passwordField.textProperty().addListener((observableValue, oldValue, newValue) -> {
      if (!newValue.equals(checkPasswordField.getText())) {
        passwordMismatchLabel.setText("Passwords do not match!");
      } else {
        passwordMismatchLabel.setText("");
      }
    });
    root.setPrefHeight(SceneConfiguration.getHeight());
    root.setPrefHeight(SceneConfiguration.getWidth());
  }
}
