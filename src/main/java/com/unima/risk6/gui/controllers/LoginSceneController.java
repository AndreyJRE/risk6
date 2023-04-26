package com.unima.risk6.gui.controllers;

import com.unima.risk6.RisikoMain;
import com.unima.risk6.database.configurations.DatabaseConfiguration;
import com.unima.risk6.database.models.User;
import com.unima.risk6.database.services.UserService;
import com.unima.risk6.gui.configurations.SceneConfiguration;
import com.unima.risk6.gui.controllers.enums.SceneName;
import com.unima.risk6.gui.scenes.LogInScene;
import com.unima.risk6.gui.scenes.SelectedUserScene;
import java.io.IOException;
import java.util.List;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javax.swing.event.ChangeListener;

public class LoginSceneController {

  private final LogInScene loginScene;

  private VBox root;

  private final UserService userService;

  private List<User> users;

  private final SceneController sceneController;

  public LoginSceneController(LogInScene loginScene) {
    this.loginScene = loginScene;
    this.sceneController = SceneConfiguration.getSceneController();
    this.userService = DatabaseConfiguration.getUserService();
  }

  public void init() {
    users = userService.getAllUsers();
    root = (VBox) loginScene.getRoot();
    root.setPrefWidth(SceneConfiguration.getWidth());
    root.setPrefHeight(SceneConfiguration.getHeight());
    Pagination usersPagination = initializeUsersPagination();
    Button createButton = createCustomCreateButton();
    Label selectUser = new Label("Select User Profile");
    selectUser.setStyle("-fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 80px; "
        + "-fx-font-weight: bold; -fx-text-fill: #2D2D2D;");
    root.getChildren().addAll(selectUser, usersPagination, createButton);
    root.setAlignment(Pos.CENTER);
    root.setSpacing(100);
  }

  private GridPane createUsersGridPanePage(List<User> usersPage) {
    GridPane usersGridPane = new GridPane();
    usersGridPane.setHgap(30);
    usersGridPane.setVgap(10);
    usersGridPane.setAlignment(Pos.CENTER);

    ImageView riskImage = new ImageView(
        new Image(getClass().getResource("/com/unima/risk6/pictures/Risk.png").toString()));


    int column = 0;
    int row = 1;

    for (User user : usersPage) {
      ImageView userImage = new ImageView(
          new Image(getClass().getResource("/com/unima/risk6/pictures" +
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
      Circle clip = new Circle(userImage.getFitWidth() / 2, userImage.getFitHeight() / 2,
          circle.getRadius());

      // apply the clip to the user image
      userImage.setClip(clip);

      // create a stack pane to place the circle and image on top of each other
      StackPane userStackPane = new StackPane();
      userStackPane.getChildren().addAll(circle, userImage);

      userStackPane.setOnMouseClicked(e -> showSelectedUser(user));

      Label userName = new Label(user.getUsername());
      userName.setStyle("-fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 20px; "
          + "-fx-font-weight: bold; -fx-text-fill: #2D2D2D;"
          + "-fx-background-color: #CCCCCC; -fx-border-color: #000000; -fx-border-radius: 20; "
          + "-fx-background-radius: 20; -fx-padding: 5 10 5 10; -fx-border-width: 2.0");
      userName.setOnMouseClicked(e -> showSelectedUser(user));

      VBox userBox = new VBox(userStackPane, userName);
      userBox.setAlignment(Pos.CENTER);
      userBox.setSpacing(-10);

      usersGridPane.add(userBox, column, row);

      column++;

      if (column % 5 == 0) {
        column = 0;
        row++;
      }
    }
    return usersGridPane;
  }

  private Pagination initializeUsersPagination() {
    int usersPerPage = 5;
    int pageCount = (int) Math.ceil(users.size() / (double) usersPerPage);

    Pagination pagination = new Pagination(pageCount, 0);
    pagination.setPageFactory((pageIndex) -> {
      int fromIndex = pageIndex * usersPerPage;
      int toIndex = Math.min(fromIndex + usersPerPage, users.size());
      List<User> usersPage = users.subList(fromIndex, toIndex);

      return createUsersGridPanePage(usersPage);
    });

    return pagination;
  }


  private Button createCustomCreateButton() {
    Button createButton = new Button("New Account needed?");
    // set the button's properties
    createButton.setAlignment(Pos.CENTER);
    createButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #000000; "
        + "-fx-font-style: italic; -fx-font-size: 24; -fx-underline: false");

// Hinzufügen von Hover-Style für Textfarbe und Unterstreichung
    createButton.setOnMouseEntered(e -> createButton.setStyle("-fx-background-color: transparent;"
        + " -fx-text-fill: #0000FF; -fx-underline: true; -fx-font-style: italic;  -fx-font-size: "
        + "24"));
    createButton.setOnMouseExited(e -> createButton.setStyle("-fx-background-color: transparent; "
        + "-fx-text-fill: #000000; -fx-underline: false; -fx-font-style: italic;  -fx-font-size: "
        + "24"));

    // add an event handler for the button
    createButton.setOnAction(event -> handleCreateButton());
    return createButton;
  }


  // define the event handler for the button
  private void handleCreateButton() {
    FXMLLoader fxmlLoader = new FXMLLoader(RisikoMain.class.getResource("fxml/CreateAccount"
        + ".fxml"));
    Parent root;
    try {
      root = fxmlLoader.load();
      Scene createScene = sceneController.getSceneBySceneName(SceneName.CREATE_ACCOUNT);
      if (createScene == null) {
        createScene = new Scene(root);
        sceneController.addScene(SceneName.CREATE_ACCOUNT, createScene);
      } else {
        createScene.setRoot(root);
      }
      sceneController.activate(SceneName.CREATE_ACCOUNT);

    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }


  public void showSelectedUser(User user) {
    SelectedUserScene scene = (SelectedUserScene) SceneConfiguration.getSceneController()
        .getSceneBySceneName(SceneName.SELECTED_USER);
    if (scene == null) {
      scene = new SelectedUserScene();
      SelectedUserSceneController selectedUserSceneController = new SelectedUserSceneController(
          scene);
      scene.setController(selectedUserSceneController);
      sceneController.addScene(SceneName.SELECTED_USER, scene);
    }
    scene.setUser(user);

    sceneController.activate(SceneName.SELECTED_USER);
  }
}
