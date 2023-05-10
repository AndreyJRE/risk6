package com.unima.risk6.gui.controllers;

import static com.unima.risk6.gui.configurations.StyleConfiguration.applyButtonStyle;
import static com.unima.risk6.gui.configurations.StyleConfiguration.generateBackArrow;
import static com.unima.risk6.gui.configurations.StyleConfiguration.showErrorDialog;

import com.unima.risk6.database.configurations.DatabaseConfiguration;
import com.unima.risk6.game.configurations.GameConfiguration;
import com.unima.risk6.game.configurations.LobbyConfiguration;
import com.unima.risk6.game.models.ServerLobby;
import com.unima.risk6.game.models.UserDto;
import com.unima.risk6.gui.configurations.SceneConfiguration;
import com.unima.risk6.gui.configurations.SessionManager;
import com.unima.risk6.gui.controllers.enums.SceneName;
import com.unima.risk6.gui.scenes.JoinOnlineScene;
import java.util.List;
import java.util.Optional;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;

public class JoinOnlineSceneController {

  private final JoinOnlineScene joinOnlineScene;
  private final SceneController sceneController;
  private BorderPane root;

  public JoinOnlineSceneController(JoinOnlineScene joinOnlineScene) {
    this.joinOnlineScene = joinOnlineScene;
    this.sceneController = SceneConfiguration.getSceneController();
  }

  public void init() {
    this.root = (BorderPane) joinOnlineScene.getRoot();
    Font.loadFont(getClass().getResourceAsStream("/com/unima/risk6/fonts/Segoe UI Bold.ttf"), 26);
    initElements();
  }

  private void initElements() {
    AnchorPane centralBox = initCentralVBox();
    // Back arrow
    Path arrow = generateBackArrow();

    // Wrap the arrow in a StackPane to handle the click event
    StackPane backButton = new StackPane(arrow);
    backButton.setOnMouseClicked(e -> sceneController.activate(SceneName.TITLE));

    root.setCenter(centralBox);
    root.setLeft(backButton);

    BorderPane.setMargin(backButton, new Insets(10, 0, 0, 10));
  }

  private AnchorPane initCentralVBox() {

    HBox hBox = new HBox();
    hBox.setAlignment(Pos.CENTER);

    AnchorPane anchorPane = new AnchorPane();
    anchorPane.setPrefSize(600, 495);
    anchorPane.setPadding(new Insets(190, 270, 190, 270));

    VBox vBox = new VBox();
    vBox.setAlignment(Pos.CENTER);
    vBox.setSpacing(22);
    vBox.setStyle(
        "-fx-background-color: #FFFFFF; -fx-background-radius: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.14), 10, 0, 0, 0);");
    AnchorPane.setTopAnchor(vBox, 0.0);
    AnchorPane.setRightAnchor(vBox, 0.0);
    AnchorPane.setBottomAnchor(vBox, 0.0);
    AnchorPane.setLeftAnchor(vBox, 0.0);

    Label titleLabel = new Label("Join Online");
    titleLabel.setFont(Font.font("BentonSans Bold", 41));
    titleLabel.setStyle(
        "-fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #2D2D2D;");

    TextField ipAdressTextField = new TextField();
    ipAdressTextField.setPrefSize(800, 40);
    ipAdressTextField.setFont(Font.font(18));
    ipAdressTextField.setPromptText("Enter IP Adress");
    ipAdressTextField.setStyle("-fx-background-radius: 20; -fx-border-radius: 20;");

    TextField portTextField = new TextField();
    portTextField.setPrefSize(800, 40);
    portTextField.setFont(Font.font(18));
    portTextField.setPromptText("Enter Port");
    portTextField.setStyle("-fx-background-radius: 20; -fx-border-radius: 20;");

    Label serverNotFoundLabel = new Label();
    serverNotFoundLabel.setFont(Font.font(14));
    serverNotFoundLabel.setTextFill(Color.RED);
    serverNotFoundLabel.setPadding(new Insets(-10, 0, -10, 0));

    Button joinButton = new Button("Join");
    joinButton.setPrefSize(1080, 40);
    joinButton.setFont(Font.font(18));
    applyButtonStyle(joinButton);

    Label ipLabel = new Label("Enter IP Adress:");
    ipLabel.setMinWidth(130);
    ipLabel.setFont(Font.font(18));
    ipLabel.setAlignment(Pos.CENTER_LEFT);

    Label portLabel = new Label("Enter Port:");
    portLabel.setMinWidth(130);
    portLabel.setFont(Font.font(18));
    portLabel.setAlignment(Pos.CENTER_LEFT);

    GridPane centerGrid = new GridPane();
    centerGrid.add(ipLabel, 0, 0);
    centerGrid.add(portLabel, 0, 1);
    centerGrid.add(ipAdressTextField, 1, 0);
    centerGrid.add(portTextField, 1, 1);
    centerGrid.setHgap(20);
    centerGrid.setVgap(15);

    vBox.getChildren().addAll(titleLabel, centerGrid, serverNotFoundLabel, joinButton);
    vBox.setPadding(new Insets(15, 15, 15, 15));

    anchorPane.getChildren().add(vBox);

    joinButton.setOnMouseClicked(
        e -> handleJoin(ipAdressTextField.getText(), Integer.parseInt(portTextField.getText())));

    return anchorPane;
  }

  private void handleJoin(String host, int port) {

    LobbyConfiguration.configureGameClient(host, port);
    LobbyConfiguration.startGameClient();
    int i = 0;
    boolean usernameExists = false;
    while (LobbyConfiguration.getGameClient().getCh() == null && i < 10) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      i++;

    }
    if (i >= 10) {
      showErrorDialog("Error",
          "Failed to connect to Server. Please correct your inputs if necessary.");
    } else {
      UserDto userDto = UserDto.mapUserAndHisGameStatistics(SessionManager.getUser(),
          DatabaseConfiguration.getGameStatisticService()
              .getAllStatisticsByUserId(SessionManager.getUser().getId()));
      GameConfiguration.setMyGameUser(userDto);
      try {
        Thread.sleep(200);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      LobbyConfiguration.sendJoinServer(userDto);

    }
    //TODO Rework this maybe
   /* String username = SessionManager.getUser().getUsername();
    ServerLobby serverLobby = LobbyConfiguration.getServerLobby();
    List<UserDto> users = serverLobby.getUsers();
    for (UserDto userDto : users) {
      if (userDto.getUsername().equals(username)) {
        usernameExists = true;
        break;
      }
    }
    if (usernameExists) {
      String newUserName = handleUsernameExists("Your username " + username
              + " already exists in this lobby. Please select a unique username.", "New username",
          "Enter your unique username: ");
      SessionManager.getUser().setUsername(newUserName);
    } */
  }

  private String handleUsernameExists(String problem, String title, String body) {
    Alert alert = new Alert(Alert.AlertType.WARNING, problem,
        ButtonType.OK);
    boolean exists = false;
    String newUsername = "";
    alert.showAndWait();
    ServerLobby serverLobby = LobbyConfiguration.getServerLobby();
    List<UserDto> users = serverLobby.getUsers();
    if (alert.getResult() == ButtonType.OK) {
      TextInputDialog dialog = new TextInputDialog();
      dialog.setTitle(title);
      dialog.setHeaderText(null);
      dialog.setContentText(body);
      Optional<String> result = dialog.showAndWait();
      if (result.isPresent()) {
        for (UserDto userDto : users) {
          if (userDto.getUsername().equals(result.get())) {
            exists = true;
            break;
          }
        }
        if (exists) {
          newUsername = handleUsernameExists("Your username " + result.get()
                  + " already exists in this lobby. Please select a unique username.", "New username",
              "Enter your unique username for this lobby: ");
        } else {
          newUsername = result.get();
        }
      }
    }
    return newUsername;
  }
}
