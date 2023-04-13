package com.unima.risk6.gui.scenes;

import com.unima.risk6.RisikoMain;
import com.unima.risk6.database.configurations.DatabaseConfiguration;
import com.unima.risk6.database.configurations.PasswordEncryption;
import com.unima.risk6.database.models.User;
import com.unima.risk6.database.services.UserService;
import java.io.IOException;
import java.util.List;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class LogInScreen {

  private UserService userService;
  private List<User> users;
  private Stage stage;

  public LogInScreen(Stage stage) {
    //TODO Change to extend scene, remove stage from here, because we have scenecontroller for it
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
    createButton.setStyle(
        "-fx-background-color: linear-gradient(#FFDAB9, #FFA07A); -fx-text-fill: #FFFFFF; -fx-background-radius: 20; -fx-border-radius: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.14), 10, 0, 0, 0);");
    createButton.setFont(new Font(18));

    // add an event handler for the button
    createButton.setOnAction(event -> handleCreateButton());

    GridPane usersGridPane = new GridPane();
    usersGridPane.setHgap(10);
    usersGridPane.setVgap(10);
    usersGridPane.setAlignment(Pos.CENTER);

    ImageView riskImage = new ImageView(new Image(getClass().getResource("/pictures/Risk.png").toString()));


    int column = 0;
    int row = 1;

    for (User user : users) {
      ImageView userImage = new ImageView(new Image(getClass().getResource("/pictures" +
          "/747376.png").toString()));
      userImage.setFitHeight(120);
      userImage.setFitWidth(120);

      // create a circle with a black outline and a grey fill
      Circle circle = new Circle();
      circle.setRadius(75);
      circle.setStroke(Color.BLACK);
      circle.setFill(Color.LIGHTGRAY);
      circle.setStrokeWidth(3.0);

      // create a clip for the user image
      Circle clip = new Circle(userImage.getFitWidth() / 2, userImage.getFitHeight() / 2, circle.getRadius());

      // apply the clip to the user image
      userImage.setClip(clip);

      // create a stack pane to place the circle and image on top of each other
      StackPane userStackPane = new StackPane();
      userStackPane.getChildren().addAll(circle, userImage);

      userStackPane.setOnMouseClicked(e -> showSelectedUser(user));

      Label userName = new Label(user.getUsername());
      userName.setStyle("-fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 20px; "
          + "-fx-font-weight: bold; -fx-text-fill: #2D2D2D;");
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
    //usersGridPane.add(createButton, 0, row + 1, 5, 1);

    VBox box = new VBox(riskImage, usersGridPane, createButton);
    box.setAlignment(Pos.CENTER);
    box.setSpacing(100);

    Scene scene = new Scene(box, 1080, 720);
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

    ImageView userImage = new ImageView(new Image(getClass().getResource("/pictures"
        + "/747376.png").toString()));
    userImage.setFitHeight(200);
    userImage.setFitWidth(200);

    Circle circle = new Circle();
    circle.setRadius(125);
    circle.setStroke(Color.BLACK);
    circle.setFill(Color.LIGHTGRAY);
    circle.setStrokeWidth(5.0);

    // create a clip for the user image
    Circle clip = new Circle(userImage.getFitWidth() / 2, userImage.getFitHeight() / 2, circle.getRadius());

    // apply the clip to the user image
    userImage.setClip(clip);

    // create a stack pane to place the circle and image on top of each other
    StackPane userStackPane = new StackPane();
    userStackPane.getChildren().addAll(circle, userImage);


    Label selectedUserName = new Label(user.getUsername());
    selectedUserName.setStyle("-fx-font-size: 24");

    PasswordField passwordField = new PasswordField();
    passwordField.setPromptText("Enter password");
    passwordField.setStyle("-fx-background-radius: 20; -fx-border-radius: 20;");

    Label welcomeBack = new Label("Welcome Back!");
    welcomeBack.setStyle("-fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #2D2D2D;");

    VBox passwordEntryBox = new VBox(welcomeBack, userStackPane, selectedUserName, passwordField);
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
    // Create a minimalistic back button using a Path with a larger arrow
    Path arrow = new Path();
    arrow.getElements().add(new MoveTo(10, 15));
    arrow.getElements().add(new LineTo(30, 0));
    arrow.getElements().add(new MoveTo(30, 30));
    arrow.getElements().add(new LineTo(10, 15));
    arrow.setStrokeWidth(3);
    arrow.setStroke(Color.BLACK);
    arrow.setFill(Color.TRANSPARENT);

    // Wrap the arrow in a StackPane to handle the click event
    StackPane backButton = new StackPane(arrow);
    backButton.setOnMouseClicked(e -> showLoginScreen());

    // Create a BorderPane to hold the backButton and passwordEntryBox
    BorderPane root = new BorderPane();

    // Set backButton to the left side of the BorderPane
    root.setLeft(backButton);

    // Add some spacing around backButton
    BorderPane.setMargin(backButton, new Insets(10, 0, 0, 10));

    // Add passwordEntryBox to the center of the BorderPane
    root.setCenter(passwordEntryBox);

    Scene scene = new Scene(root, 1080, 720);
    stage.setScene(scene);
  }
}
