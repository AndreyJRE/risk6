package com.unima.risk6.gui.controllers;

import static com.unima.risk6.gui.configurations.SoundConfiguration.pauseTitleSound;
import static com.unima.risk6.gui.configurations.StyleConfiguration.applyButtonStyle;
import static com.unima.risk6.gui.configurations.StyleConfiguration.generateBackArrow;

import com.unima.risk6.database.configurations.DatabaseConfiguration;
import com.unima.risk6.game.configurations.GameConfiguration;
import com.unima.risk6.game.configurations.LobbyConfiguration;
import com.unima.risk6.game.models.UserDto;
import com.unima.risk6.gui.configurations.SceneConfiguration;
import com.unima.risk6.gui.configurations.SessionManager;
import com.unima.risk6.gui.controllers.enums.SceneName;
import com.unima.risk6.gui.scenes.JoinOnlineScene;
import com.unima.risk6.gui.scenes.SelectMultiplayerLobbyScene;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
    Font.loadFont(getClass().getResourceAsStream("/com/unima/risk6/Fonts/Fonts/Segoe UI Bold.ttf"),
        26);
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
    Task<Void> task = new Task<Void>() {
      @Override
      protected Void call() throws Exception {
        LobbyConfiguration.configureGameClient(host, port);
        LobbyConfiguration.startGameClient();
        while (LobbyConfiguration.getGameClient() == null) {
          Thread.sleep(100);
        }
        return null;
      }
    };
    Thread thread = new Thread(task);
    thread.start();
    UserDto userDto = UserDto.mapUserAndHisGameStatistics(SessionManager.getUser(),
        DatabaseConfiguration.getGameStatisticService()
            .getAllStatisticsByUserId(SessionManager.getUser().getId()));
    GameConfiguration.setMyGameUser(userDto);
    //TODO Auskommentieren
    //Platform.runLater(() -> LobbyConfiguration.sendJoinServer(userDto));
    SelectMultiplayerLobbyScene scene = (SelectMultiplayerLobbyScene) SceneConfiguration.getSceneController()
        .getSceneBySceneName(SceneName.SELECT_LOBBY);
    if (scene == null) {
      scene = new SelectMultiplayerLobbyScene();
      SelectMultiplayerLobbySceneController selectMultiplayerLobbySceneController = new SelectMultiplayerLobbySceneController(
          scene);
      scene.setController(selectMultiplayerLobbySceneController);
      sceneController.addScene(SceneName.SELECT_LOBBY, scene);
    }
    pauseTitleSound();
    sceneController.activate(SceneName.SELECT_LOBBY);
  }

  //TODO:

}
