package com.unima.risk6.gui.scenes;

import com.unima.risk6.game.logic.GameState;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

public class GameScene extends Scene {

//  private GameState gameState;
//
//  private Queue<PlayerUI> playerUIS;
//
//  private Stack<CardUI> cardUIS;

  private Set<CountryUI> countriesUI;

  private double originalScreenWidth;

  private double originalScreenHeight;

  public GameScene(Parent parent,
      int width,
      int height,
//      GameState gameState,
//      Queue<PlayerUI> playerUIS,
//      Stack<CardUI> cardUIS
      Set<CountryUI> countriesUI) {
    super(new Pane(), width, height);
    this.originalScreenWidth = width;
    this.originalScreenHeight = height;
//    this.gameState = gameState;
//    this.playerUIS = playerUIS;
//    this.cardUIS = cardUIS;
    this.countriesUI = countriesUI;

    Group countriesGroup = new Group();
    countriesGroup.getChildren().addAll(countriesUI);

    Pane pane = (Pane) getRoot();
    pane.getChildren().add(countriesGroup);

    double widthRatio = getWidth() / originalScreenWidth;
    double heightRatio = getHeight() / originalScreenHeight;
    double initialScale = Math.min(widthRatio, heightRatio);

    countriesGroup.setScaleX(initialScale);
    countriesGroup.setScaleY(initialScale);

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

    javafx.geometry.Bounds totalBounds = countriesGroup.getBoundsInParent();
    countriesGroup.setTranslateX((getWidth() - totalBounds.getWidth() * initialScale) / 2.0
        - totalBounds.getMinX() * initialScale);
    countriesGroup.setTranslateY((getHeight() - totalBounds.getHeight() * initialScale) / 2.0
        - totalBounds.getMinY() * initialScale);
  }
}

