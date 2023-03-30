package com.unima.risk6.gui.scenes;

import com.unima.risk6.game.logic.GameState;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
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

  public GameScene(Parent parent,
      int width,
      int height,
//      GameState gameState,
//      Queue<PlayerUI> playerUIS,
//      Stack<CardUI> cardUIS
      Set<CountryUI> countriesUI) {
    super(new Pane(), width, height);
//    this.gameState = gameState;
//    this.playerUIS = playerUIS;
//    this.cardUIS = cardUIS;
    this.countriesUI = countriesUI;

    widthProperty().addListener((obs, oldVal, newVal) -> {

      double oldCenterX = oldVal.doubleValue() / 2.0;
      double newCenterX = newVal.doubleValue() / 2.0;
      double deltaX = newCenterX - oldCenterX;

      for (CountryUI country : countriesUI) {
        country.setLayoutX(country.getLayoutX() + deltaX);
      }
    });
    heightProperty().addListener((obs, oldVal, newVal) -> {

      double oldCenterY = oldVal.doubleValue() / 2.0;
      double newCenterY = newVal.doubleValue() / 2.0;
      double deltaY = newCenterY - oldCenterY;

      for (CountryUI country : countriesUI) {
        country.setLayoutY(country.getLayoutY() + deltaY);
      }
    });

    Pane pane = (Pane) getRoot();
    for (CountryUI country : countriesUI) {
      pane.getChildren().add(country);
    }

  }
}
