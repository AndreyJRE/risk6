package com.unima.risk6.gui.scenes;

import com.unima.risk6.gui.uiModels.ActivePlayerUi;
import com.unima.risk6.gui.uiModels.CountryUi;
import com.unima.risk6.gui.uiModels.PlayerUi;
import com.unima.risk6.gui.uiModels.TimeUi;
import java.util.ArrayList;
import java.util.Set;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class GameScene extends Scene {

//  private GameState gameState;
//
//  private Stack<CardUi> cardUis;

  private BorderPane root;

  private Set<CountryUi> countriesUis;

  private double originalScreenWidth;

  private double originalScreenHeight;

  private ActivePlayerUi ellipseWithRectangle;

  private ArrayList<PlayerUi> PlayerUis;

  private com.unima.risk6.gui.uiModels.TimeUi TimeUi;


  public GameScene(
//  GameState gameState,
//  Stack<CardUi> cardUIS
//  this.gameState = gameState;
//  this.playerUIS = playerUIS;
//  this.cardUIS = cardUIS;
      int width,
      int height,
      Set<CountryUi> countriesUis,
      ActivePlayerUi ellipseWithRectangle,
      ArrayList<PlayerUi> PlayerUis, TimeUi TimeUi) {
    super(new BorderPane(), width, height);
    this.originalScreenWidth = width;
    this.originalScreenHeight = height;
    this.countriesUis = countriesUis;
    this.ellipseWithRectangle = ellipseWithRectangle;
    this.PlayerUis = PlayerUis;
    this.TimeUi = TimeUi;

    this.root = (BorderPane) getRoot();
    Group countriesGroup = new Group();

    this.initializeGameScene(countriesGroup, PlayerUis);

    this.addListeners(countriesGroup);
  }

  private void initializeGameScene(Group countriesGroup, ArrayList<PlayerUi> PlayerUis) {

    countriesGroup.getChildren().addAll(countriesUis);
    Pane countriesPane = new Pane();
    countriesPane.getChildren().add(countriesGroup);
    root.setCenter(countriesPane);

    VBox playersVbox = new VBox();
    playersVbox.getChildren().addAll(PlayerUis);
    playersVbox.setAlignment(Pos.CENTER);
    StackPane playerPane = new StackPane();
    playerPane.getChildren().add(playersVbox);
    playerPane.setPadding(new Insets(0, 0, 0, 0));
    root.setLeft(playerPane);

    StackPane timePane = new StackPane();
    timePane.getChildren().add(TimeUi);
    timePane.setAlignment(Pos.CENTER);
    timePane.setPadding(new Insets(0, 10, 0, 0));
    root.setRight(timePane);

    StackPane bottomPane = new StackPane();
    bottomPane.getChildren().add(ellipseWithRectangle);
    bottomPane.setAlignment(Pos.CENTER);
    bottomPane.setPadding(new Insets(0, 0, 30, 0));
    root.setBottom(bottomPane);

    double widthRatio = getWidth() / originalScreenWidth;
    double heightRatio = getHeight() / originalScreenHeight;
    double initialScale = Math.min(widthRatio, heightRatio);

    countriesGroup.setScaleX(initialScale);
    countriesGroup.setScaleY(initialScale);

    javafx.geometry.Bounds totalBounds = countriesGroup.getBoundsInParent();
    countriesGroup.setTranslateX((getWidth() - totalBounds.getWidth() * initialScale) / 2.0
        - totalBounds.getMinX() * initialScale);
    countriesGroup.setTranslateY((getHeight() - totalBounds.getHeight() * initialScale) / 2.0
        - totalBounds.getMinY() * initialScale);
  }


  private void addListeners(Group countriesGroup) {
    widthProperty().addListener((obs, oldVal, newVal) -> {
      double widthScale = newVal.doubleValue() / originalScreenWidth;
      double heightScale = getHeight() / originalScreenHeight;
      double scale = Math.min(widthScale, heightScale);

      countriesGroup.setScaleX(scale);
      countriesGroup.setScaleY(scale);

      double newCenterX = newVal.doubleValue() / 2.0;
      double oldCenterX = oldVal.doubleValue() / 2.0;
      double deltaX = newCenterX - oldCenterX;
      countriesGroup.setTranslateX(countriesGroup.getTranslateX() + deltaX);

    });

    heightProperty().addListener((obs, oldVal, newVal) -> {
      double widthScale = getWidth() / originalScreenWidth;
      double heightScale = newVal.doubleValue() / originalScreenHeight;
      double scale = Math.min(widthScale, heightScale);

      countriesGroup.setScaleX(scale);
      countriesGroup.setScaleY(scale);

      double newCenterY = newVal.doubleValue() / 2.0;
      double oldCenterY = oldVal.doubleValue() / 2.0;
      double deltaY = newCenterY - oldCenterY;
      countriesGroup.setTranslateY(countriesGroup.getTranslateY() + deltaY);
    });
  }
}

