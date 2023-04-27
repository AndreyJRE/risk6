package com.unima.risk6.gui.controllers;

public class GameSceneController {

  @Override
  public void update(GameState gameState) {
    this.gameState = gameState;
    List<Move> lastMoves = gameState.getLastMoves();
    lastMoves.forEach(lastMove -> {
      if (lastMove instanceof Reinforce reinforce
          && gameState.getCurrentPlayer().getCurrentPhase() == GamePhase.CLAIM_PHASE) {

      }
    });

  }
}
