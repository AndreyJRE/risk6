package com.unima.risk6.gui.scenes;

import com.unima.risk6.RisikoMain;
import com.unima.risk6.database.configurations.DatabaseConfiguration;
import com.unima.risk6.database.models.User;
import com.unima.risk6.database.services.UserService;
import com.unima.risk6.gui.controllers.SceneController;
import com.unima.risk6.gui.controllers.enums.SceneName;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class LogInScreen {

  private UserService userService;
  private List<User> users;
  private Stage stage;

  public LogInScreen(Stage stage) {
    this.stage = stage;
    this.userService = DatabaseConfiguration.getUserService();
    this.users = userService.getAllUsers();
    showLoginScreen();
  }

  private void showLoginScreen() {
    GridPane usersGridPane = new GridPane();
    usersGridPane.setHgap(10);
    usersGridPane.setVgap(10);
    usersGridPane.setAlignment(Pos.CENTER);

    int column = 0;
    int row = 0;

    for (User user : users) {
      ImageView userImage = new ImageView(new Image(user.getImagePath()));
      userImage.setFitHeight(100);
      userImage.setFitWidth(100);
      userImage.setOnMouseClicked(e -> showSelectedUser(user));

      Label userName = new Label(user.getUsername());
      userName.setOnMouseClicked(e -> showSelectedUser(user));

      VBox userBox = new VBox(userImage, userName);
      userBox.setAlignment(Pos.CENTER);

      usersGridPane.add(userBox, column, row);

      column++;

      if (column % 5 == 0) {
        column = 0;
        row++;
      }
    }

    Scene scene = new Scene(usersGridPane, 800, 600);
    stage.setScene(scene);
  }

  private void showSelectedUser(User user) {
    ImageView selectedUserImage = new ImageView(new Image(user.getImagePath()));
    selectedUserImage.setFitHeight(200);
    selectedUserImage.setFitWidth(200);

    Label selectedUserName = new Label(user.getUsername());
    selectedUserName.setStyle("-fx-font-size: 24");

    PasswordField passwordField = new PasswordField();
    passwordField.setPromptText("Enter password");

    VBox passwordEntryBox = new VBox(selectedUserImage, selectedUserName, passwordField);
    passwordEntryBox.setAlignment(Pos.CENTER);
    passwordEntryBox.setSpacing(20);

    passwordField.setOnAction(e -> {
      String enteredPassword = passwordField.getText();
      if (enteredPassword.equals(user.getPassword())) {
        // Proceed to the next scene (game) that is created with FXML
        try {
          FXMLLoader fxmlLoader = new FXMLLoader(RisikoMain.class.getResource("fxml/TitleScreen"
              + ".fxml"));
          Parent root = fxmlLoader.load();
          Scene nextScene = new Scene(root, 800, 600);
          stage.setScene(nextScene);
        } catch (IOException ex) {
          throw new RuntimeException(ex);
        }
      } else {
        // TODO: Show an error message (incorrect password)
        System.out.println("Wrong password!");
      }
    });

    Scene scene = new Scene(passwordEntryBox, 800, 600);
    stage.setScene(scene);
  }
}
