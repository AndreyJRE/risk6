package com.unima.risk6.gui;

import com.unima.risk6.game.logic.GameState;
import java.util.Queue;
import java.util.Stack;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class GameScene extends Scene {

  private GameState gameState;

  private Queue<PlayerUI> playerUIS;

  private Stack<CardUI> cardUIS;

  public GameScene(Parent parent,
      GameState gameState,
      Queue<PlayerUI> playerUIS,
      Stack<CardUI> cardUIS) {
    super(parent);
    this.gameState = gameState;
    this.playerUIS = playerUIS;
    this.cardUIS = cardUIS;

  }
}
