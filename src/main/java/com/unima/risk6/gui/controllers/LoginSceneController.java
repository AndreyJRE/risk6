package com.unima.risk6.gui.controllers;

import com.unima.risk6.RisikoMain;
import com.unima.risk6.database.configurations.DatabaseConfiguration;
import com.unima.risk6.database.models.User;
import com.unima.risk6.database.services.UserService;
import com.unima.risk6.gui.configurations.ImageConfiguration;
import com.unima.risk6.gui.configurations.SceneConfiguration;
import com.unima.risk6.gui.configurations.SessionManager;
import com.unima.risk6.gui.controllers.enums.ImageName;
import com.unima.risk6.gui.controllers.enums.SceneName;
import com.unima.risk6.gui.scenes.LogInScene;
import com.unima.risk6.gui.scenes.SelectedUserScene;
import java.io.IOException;
import java.util.List;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Controller for the login scene, handling user interactions and updating the view accordingly.
 *
 * @author fisommer
 */

public class LoginSceneController {

  private final LogInScene loginScene;

  private VBox root;

  private final UserService userService;

  private List<User> users;

  private final SceneController sceneController;

  private DropShadow dropShadow;

  /**
   * Constructs a LoginSceneController with a given LogInScene.
   *
   * @param loginScene the login scene to be managed by this controller
   */

  public LoginSceneController(LogInScene loginScene) {
    this.loginScene = loginScene;
    this.sceneController = SceneConfiguration.getSceneController();
    this.userService = DatabaseConfiguration.getUserService();
  }

  /**
   * Initializes the scene, sets up the user interface and loads all users from the UserService.
   */

  public void init() {
    users = userService.getAllUsers();
    root = (VBox) loginScene.getRoot();
    root.setPrefWidth(SceneConfiguration.getWidth());
    root.setPrefHeight(SceneConfiguration.getHeight());
    dropShadow = new DropShadow();
    dropShadow.setRadius(15.0);
    dropShadow.setColor(Color.LIGHTSKYBLUE.darker());
    Pagination usersPagination = initializeUsersPagination();
    Button createButton = createCustomCreateButton();
    Label selectUser = new Label("Select User Profile");
    selectUser.setStyle("-fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 80px; "
        + "-fx-font-weight: bold; -fx-text-fill: white;");
    root.getChildren().addAll(selectUser, usersPagination, createButton);
    root.setAlignment(Pos.CENTER);
    root.setSpacing(75);
    Image originalImage = ImageConfiguration.getImageByName(ImageName.LOGIN_BACKGROUND);
    javafx.scene.image.ImageView imageView = new javafx.scene.image.ImageView(originalImage);

    // Set the opacity
    imageView.setOpacity(0.9);

    // Create a snapshot of the ImageView
    SnapshotParameters parameters = new SnapshotParameters();
    parameters.setFill(Color.TRANSPARENT);
    Image semiTransparentImage = imageView.snapshot(parameters, null);

    // Use the semi-transparent image for the background
    BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, true);
    BackgroundImage backgroundImage = new BackgroundImage(semiTransparentImage,
        BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
        backgroundSize);
    Background background = new Background(backgroundImage);
    root.setBackground(background);
  }

  /**
   * Creates a GridPane page of users to be displayed.
   *
   * @param usersPage the list of users to be included on this page
   * @return the created GridPane
   */

  private GridPane createUsersGridPanePage(List<User> usersPage) {
    GridPane usersGridPane = new GridPane();
    usersGridPane.setHgap(30);
    usersGridPane.setVgap(10);
    usersGridPane.setAlignment(Pos.CENTER);

    int column = 0;
    int row = 1;

    for (User user : usersPage) {
      ImageView userImage = new ImageView(
          new Image(getClass().getResource(user.getImagePath()).toString()));
      userImage.setFitHeight(150);
      userImage.setFitWidth(150);

      // create a circle with a black outline and a grey fill
      Circle circle = new Circle();
      circle.setRadius(75);
      circle.setStroke(Color.BLACK);
      circle.setFill(Color.WHITESMOKE);
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
      userStackPane.hoverProperty().addListener((observableValue, aboolean, t1) -> {
        if (t1) {
          userStackPane.setCursor(Cursor.HAND);
          userStackPane.setEffect(dropShadow);
        } else {
          userStackPane.setCursor(Cursor.DEFAULT);
          userStackPane.setEffect(null);
        }
      });
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

  /**
   * Initializes the Pagination component for navigating through users.
   *
   * @return the initialized Pagination
   */

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

  /**
   * Creates a custom Button for creating a new account.
   *
   * @return the created Button
   */

  private Button createCustomCreateButton() {
    Button createButton = new Button("New Account needed?");
    // set the button's properties
    createButton.setAlignment(Pos.CENTER);
    createButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white; "
        + "-fx-font-style: italic; -fx-font-size: 24; -fx-underline: false");

    // Hinzufügen von Hover-Style für Textfarbe und Unterstreichung
    createButton.setOnMouseEntered(e -> createButton.setStyle("-fx-background-color: transparent;"
        + " -fx-text-fill: white; -fx-underline: true; -fx-font-style: italic;  -fx-font-size: "
        + "24"));
    createButton.setOnMouseExited(e -> createButton.setStyle("-fx-background-color: transparent; "
        + "-fx-text-fill: white; -fx-underline: false; -fx-font-style: italic;  -fx-font-size: "
        + "24"));

    // add an event handler for the button
    createButton.setOnAction(event -> handleCreateButton());
    return createButton;
  }


  /**
   * Defines the action to be taken when the 'Create Account' button is clicked.
   */

  private void handleCreateButton() {
    FXMLLoader fxmlLoader = new FXMLLoader(
        RisikoMain.class.getResource("fxml/CreateAccount" + ".fxml"));
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

  /**
   * Shows the scene of the selected user.
   *
   * @param user the selected User
   */

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
    SessionManager.setUser(user);

    sceneController.activate(SceneName.SELECTED_USER);
  }
}
