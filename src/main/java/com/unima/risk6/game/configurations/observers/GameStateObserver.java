package com.unima.risk6.game.configurations.observers;

import com.unima.risk6.game.models.GameState;

/**
 * The GameStateObserver defines a method that allows an observer to update the game state when the
 * client receives a new game state.
 *
 * @author astoyanov
 */
public interface GameStateObserver {

  /**
   * Updates the local server lobby based on the provided server lobby.
   *
   * @param gameState The new game lobby.
   */
  void update(GameState gameState);

}
