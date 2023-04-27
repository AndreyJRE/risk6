package com.unima.risk6.gui.scenes;


import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.game.models.enums.PlayerColor;
import com.unima.risk6.gui.uiModels.ActivePlayerUi;
import com.unima.risk6.gui.uiModels.CountryUi;
import com.unima.risk6.gui.uiModels.PlayerUi;
import com.unima.risk6.gui.uiModels.TimeUi;
import com.unima.risk6.gui.uiModels.TroopsCounterUi;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.SVGPath;
import javafx.stage.Popup;
import javafx.util.Duration;

public class GameScene extends Scene implements Initializable {

  private GameState gameState;
//
//  private Stack<CardUi> cardUis;

  private BorderPane root;

  private Set<CountryUi> countriesUis;

  private double originalScreenWidth;

  private double originalScreenHeight;

  private BorderPane chatBoxPane;

  private Group countriesGroup;

  private boolean isCountrySelectedToAttackOthers;

  public GameScene(
      GameState gameState,
//  Stack<CardUi> cardUiS
//  this.gameState = gameState;
//  this.cardUiS = cardUiS;
      int width,
      int height,
      Set<CountryUi> countriesUis) {
    super(new BorderPane(), width, height);
    this.gameState = gameState;
    this.originalScreenWidth = width;
    this.originalScreenHeight = height;
    this.countriesUis = countriesUis;
    this.root = (BorderPane) getRoot();
    this.countriesGroup = new Group();
    this.chatBoxPane = new BorderPane();

    this.initializeGameScene();

    this.addListeners();
  }

  private void initializeGameScene() {

    StackPane countriesPane = initializeCountriesPane();
    root.setCenter(countriesPane);
    StackPane playerPane = initializePlayersPane();
    root.setLeft(playerPane);
    StackPane timePane = initializeTimePane();
    root.setRight(timePane);
    StackPane bottomPane = initializeBottomPane();
    root.setBottom(bottomPane);
  }

  private StackPane initializeCountriesPane() {
    double widthRatio = getWidth() / originalScreenWidth;
    double heightRatio = getHeight() / originalScreenHeight;
    double initialScale = Math.min(widthRatio, heightRatio);

    countriesGroup.getChildren().addAll(countriesUis);
    addMouseHandlers(countriesGroup);
    StackPane countriesPane = new StackPane();

    for (CountryUi countryUi : countriesUis) {
      Bounds bounds = countryUi.getCountryPath().getBoundsInParent();
      double ellipseX = bounds.getMinX() + bounds.getWidth() * 0.5;
      double ellipseY = bounds.getMinY() + bounds.getHeight() * 0.5;

      Point2D correctedCoordinates = CountryUi.correctEllipsePlacement(countryUi.getCountry(),
          ellipseX, ellipseY);
      ellipseX = correctedCoordinates.getX();
      ellipseY = correctedCoordinates.getY();

      TroopsCounterUi troopsCounterUi = new TroopsCounterUi(ellipseX, ellipseY);

      countryUi.setTroopsCounterUi(troopsCounterUi);

      countriesGroup.getChildren().add(troopsCounterUi);

    }
    countriesGroup.setScaleX(initialScale);
    countriesGroup.setScaleY(initialScale);

    countriesPane.getChildren().add(countriesGroup);
    return countriesPane;
  }

  public void setGameSceneController(GameSceneController gameSceneController) {
    this.gameSceneController = gameSceneController;
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {

  }
}

