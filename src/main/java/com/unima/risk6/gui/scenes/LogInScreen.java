package com.unima.risk6.gui.scenes;

import com.unima.risk6.RisikoMain;
import com.unima.risk6.database.configurations.DatabaseConfiguration;
import com.unima.risk6.database.configurations.PasswordEncryption;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
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
    // create a new button
    Button createButton = new Button("Create new user");

    // set the button's properties
    createButton.setPrefWidth(470);
    createButton.setPrefHeight(40);
    createButton.setAlignment(Pos.CENTER);
    createButton.setStyle("-fx-background-color: linear-gradient(#FFDAB9, #FFA07A); -fx-text-fill: #FFFFFF; -fx-background-radius: 20; -fx-border-radius: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.14), 10, 0, 0, 0);");
    createButton.setFont(new Font(18));

    // add an event handler for the button
    createButton.setOnAction(event -> handleCreateButton());

    GridPane usersGridPane = new GridPane();
    usersGridPane.setHgap(10);
    usersGridPane.setVgap(10);
    usersGridPane.setAlignment(Pos.CENTER);

    int column = 0;
    int row = 0;

    for (User user : users) {
      ImageView userImage = new ImageView(new Image(getClass().getResource("/Pictures" +
          "/747376.png").toString()));
      userImage.setFitHeight(100);
      userImage.setFitWidth(100);

      // create a circle with a black outline and a grey fill
      Circle circle = new Circle();
      circle.setRadius(50);
      circle.setStroke(Color.BLACK);
      circle.setFill(Color.LIGHTGRAY);

      // create a stack pane to place the circle and image on top of each other
      StackPane userStackPane = new StackPane();
      userStackPane.getChildren().addAll(circle, userImage);

      userStackPane.setOnMouseClicked(e -> showSelectedUser(user));

      Label userName = new Label(user.getUsername());
      userName.setOnMouseClicked(e -> showSelectedUser(user));

      VBox userBox = new VBox(userStackPane, userName);
      userBox.setAlignment(Pos.CENTER);

      usersGridPane.add(userBox, column, row);

      column++;

      if (column % 5 == 0) {
        column = 0;
        row++;
      }
    }

    // add the button to the scene
    usersGridPane.add(createButton, 0, row + 1, 5, 1);

    Scene scene = new Scene(usersGridPane, 1080, 720);
    stage.setScene(scene);
  }


  // define the event handler for the button
  private void handleCreateButton() {
    FXMLLoader fxmlLoader = new FXMLLoader(RisikoMain.class.getResource("fxml/CreateAccount"
        + ".fxml"));
    Parent root = null;
    try {
      root = fxmlLoader.load();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    Scene nextScene = new Scene(root, 1080, 720);
    stage.setScene(nextScene);
  }


  private void showSelectedUser(User user) {
    ImageView selectedUserImage = new ImageView(new Image(getClass().getResource("/Pictures"
        + "/747376.png").toString()));
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
      if (PasswordEncryption.validatePassword(enteredPassword, user.getPassword())) {
        // Proceed to the next scene (game) that is created with FXML
        try {
          FXMLLoader fxmlLoader = new FXMLLoader(RisikoMain.class.getResource("fxml/TitleScreen"
              + ".fxml"));
          Parent root = fxmlLoader.load();
          Scene nextScene = new Scene(root, 1080, 720);
          stage.setScene(nextScene);
        } catch (IOException ex) {
          throw new RuntimeException(ex);
        }
      } else {
        // TODO: Show an error message (incorrect password)
        System.out.println("Wrong password!");
      }
    });

    Scene scene = new Scene(passwordEntryBox, 1080, 720);
    stage.setScene(scene);
  }
}
