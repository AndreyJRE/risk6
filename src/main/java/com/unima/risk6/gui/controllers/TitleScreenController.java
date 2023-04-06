package com.unima.risk6.gui.controllers;

import com.unima.risk6.gui.configurations.CountriesUIConfiguration;
import com.unima.risk6.gui.controllers.enums.SceneName;
import com.unima.risk6.gui.scenes.ActivePlayerUi;
import com.unima.risk6.gui.scenes.GameScene;
import com.unima.risk6.gui.scenes.PlayerUi;
import com.unima.risk6.gui.scenes.SceneConfiguration;
import com.unima.risk6.gui.scenes.TimeUi;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class TitleScreenController implements Initializable {

  private static final String COUNTRIES_JSON_PATH = "/com/unima/risk6/json/countriesUI.json";

  @FXML
  private AnchorPane root;

  @FXML
  private ImageView backgroundImageView;

  @FXML
  private VBox vBox;

  @FXML
  private HBox hBox;

  @FXML
  private VBox titleBox;

  @FXML
  private Label titleLabel;

  @FXML
  private Button singlePlayerButton;

  @FXML
  private Button multiPlayerButton;

  @FXML
  private Button optionsButton;

  @FXML
  private Button quitButton;

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    // Set the font of the title label
    titleLabel.setFont(Font.font("72 Bold Italic", 96.0));

    // Set the style of the buttons
    String buttonStyle = "-fx-background-color: linear-gradient(#FFDAB9, #FFA07A); -fx-background-radius: 40; -fx-border-radius: 40; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.14), 10, 0, 0, 0); -fx-text-fill: #FFFFFF;";
    singlePlayerButton.setStyle(buttonStyle);
    multiPlayerButton.setStyle(buttonStyle);
    optionsButton.setStyle(buttonStyle);
    quitButton.setStyle(buttonStyle);
  }

  // Define the event handler for the single player button
  @FXML
  private void handleSinglePlayer() {
    // TODO: Implement the single player game
    System.out.println("Single player game started");
    SceneController sceneController = SceneConfiguration.getSceneController();

    Pane pane = new Pane();
    CountriesUIConfiguration countriesUIConfiguration =
        new CountriesUIConfiguration(COUNTRIES_JSON_PATH);
    countriesUIConfiguration.configureCountries();

    //dummy initialisation of Players with dummyvalue
    ArrayList<PlayerUi> PlayerUis = new ArrayList<PlayerUi>();
    int amountOfPlayers = 4;
    for (int i = 0; i < 4; i++) {
      PlayerUis.add(new PlayerUi(35,
          35, 100, 45));
    }

    Scene gameScene = new GameScene(
        1080,
        720,
        countriesUIConfiguration.getCountriesUIs(),
        new ActivePlayerUi(40,
            40, 280, 50),
        PlayerUis, new TimeUi(40, 40));

    sceneController.addScene(SceneName.GAME_SCREEN, gameScene);
    sceneController.activate(SceneName.GAME_SCREEN);
  }


  // Define the event handler for the multi player button
  @FXML
  private void handleMultiPlayer() {
    // TODO: Implement the multi player game
    System.out.println("Multi player game started");
  }

  // Define the event handler for the options button
  @FXML
  private void handleOptions() {
    // TODO: Implement the options screen
    System.out.println("Options screen opened");
  }

  @FXML
  private void handleQuitGame() {
    SceneController sceneController = SceneConfiguration.getSceneController();
    sceneController.close();
  }
}
