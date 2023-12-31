package com.unima.risk6.gui.controllers;

import static com.unima.risk6.gui.configurations.StyleConfiguration.applyButtonStyle;
import static com.unima.risk6.gui.configurations.StyleConfiguration.generateBackArrow;
import static com.unima.risk6.gui.configurations.StyleConfiguration.showErrorDialog;

import com.unima.risk6.database.configurations.DatabaseConfiguration;
import com.unima.risk6.game.configurations.GameConfiguration;
import com.unima.risk6.game.configurations.LobbyConfiguration;
import com.unima.risk6.game.models.UserDto;
import com.unima.risk6.gui.configurations.ImageConfiguration;
import com.unima.risk6.gui.configurations.SceneConfiguration;
import com.unima.risk6.gui.configurations.SessionManager;
import com.unima.risk6.gui.controllers.enums.ImageName;
import com.unima.risk6.gui.controllers.enums.SceneName;
import com.unima.risk6.gui.scenes.JoinOnlineScene;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;

/**
 * Controller for handling operations related to joining an online game. This includes
 * initialization of the join online scene and handle joining the game.
 *
 * @author fisommer
 * @author astoyano
 */

public class JoinOnlineSceneController {

  private final JoinOnlineScene joinOnlineScene;
  private final SceneController sceneController;
  private BorderPane root;

  /**
   * Constructs a new JoinOnlineSceneController.
   *
   * @param joinOnlineScene the scene for joining online games
   */

  public JoinOnlineSceneController(JoinOnlineScene joinOnlineScene) {
    this.joinOnlineScene = joinOnlineScene;
    this.sceneController = SceneConfiguration.getSceneController();
  }

  /**
   * Initializes the join online scene with the appropriate UI elements.
   */

  public void init() {
    this.root = (BorderPane) joinOnlineScene.getRoot();
    Font.loadFont(getClass().getResourceAsStream("/com/unima/risk6/fonts/segoe_ui_bold.ttf"),
        26);
    initElements();
  }

  /**
   * Initializes the elements of the join online scene.
   */

  private void initElements() {
    // Back arrow
    Path arrow = generateBackArrow();

    // Wrap the arrow in a StackPane to handle the click event
    StackPane backButton = new StackPane(arrow);
    backButton.setOnMouseClicked(e -> sceneController.activate(SceneName.TITLE));

    // Load the image into an ImageView
    Image originalImage = ImageConfiguration.getImageByName(ImageName.MULTIPLAYER_BACKGROUND);
    ImageView imageView = new ImageView(originalImage);

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
    AnchorPane centralBox = initCentralVbox();

    root.setCenter(centralBox);
    root.setLeft(backButton);

    BorderPane.setMargin(backButton, new Insets(10, 0, 0, 10));
  }

  /**
   * Initializes a VBox to be placed in the center of the scene.
   *
   * @return the initialized VBox
   */

  private AnchorPane initCentralVbox() {

    HBox hbox = new HBox();
    hbox.setAlignment(Pos.CENTER);

    AnchorPane anchorPane = new AnchorPane();
    anchorPane.setPrefSize(600, 495);
    anchorPane.setPadding(new Insets(190, 270, 190, 270));

    VBox vbox = new VBox();
    vbox.setAlignment(Pos.CENTER);
    vbox.setSpacing(22);
    vbox.setStyle(
        "-fx-opacity:0.9; -fx-background-color: #FFFFFF; -fx-background-radius: 20; "
            + "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.14), 10, 0, 0, 0);");
    AnchorPane.setTopAnchor(vbox, 0.0);
    AnchorPane.setRightAnchor(vbox, 0.0);
    AnchorPane.setBottomAnchor(vbox, 0.0);
    AnchorPane.setLeftAnchor(vbox, 0.0);

    Label titleLabel = new Label("Join Online");
    titleLabel.setFont(Font.font("BentonSans Bold", 41));
    titleLabel.setStyle(
        "-fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 36px; -fx-font-weight: bold; "
            + "-fx-text-fill: #2D2D2D;");

    TextField ipAdressTextField = new TextField();
    ipAdressTextField.setPrefSize(800, 40);
    ipAdressTextField.setFont(Font.font(18));
    ipAdressTextField.setPromptText("Enter IP Adress");
    ipAdressTextField.setStyle("-fx-background-radius: 20; -fx-border-radius: 20;");

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
    centerGrid.add(ipAdressTextField, 0, 1);
    centerGrid.setHgap(20);
    centerGrid.setVgap(15);

    vbox.getChildren().addAll(titleLabel, centerGrid, serverNotFoundLabel, joinButton);
    vbox.setPadding(new Insets(15, 15, 15, 15));

    anchorPane.getChildren().add(vbox);

    ipAdressTextField.setOnAction(
        e -> handleJoin(ipAdressTextField.getText()));

    joinButton.setOnAction(
        e -> handleJoin(ipAdressTextField.getText()));

    return anchorPane;
  }

  /**
   * Handles the join request when the player wants to join a game.
   *
   * @param host the host address of the game server
   */

  private void handleJoin(String host) {

    LobbyConfiguration.configureGameClient(host);
    LobbyConfiguration.startGameClient();
    int i = 0;
    while (LobbyConfiguration.getGameClient().getHandler() == null && i < 20) {
      try {
        Thread.sleep(50);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      i++;
    }
    if (i >= 20) {
      showErrorDialog("Connection error.", "Please start the game again.");
      return;
    }
    i = 0;
    while (!LobbyConfiguration.getGameClient().isHandshakeComplete() && i < 20) {
      try {
        Thread.sleep(50);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      i++;

    }
    if (i >= 20) {
      showErrorDialog("Connection error.", "Please start the game again.");
      return;
    }
    UserDto userDto = UserDto.mapUserAndHisGameStatistics(SessionManager.getUser(),
        DatabaseConfiguration.getGameStatisticService()
            .getAllStatisticsByUserId(SessionManager.getUser().getId()));
    GameConfiguration.setMyGameUser(userDto);
    LobbyConfiguration.sendJoinServer(userDto);

  }
}

