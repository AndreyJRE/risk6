package com.unima.risk6.game.configurations.observers;

import com.unima.risk6.game.models.GameLobby;

/**
 * The GameLobbyObserver defines a method that allows an observer to update the game lobby when the
 * client receives a new game lobby.
 *
 * @author astoyanov
 */
public interface GameLobbyObserver {

  /**
   * Updates the local server lobby based on the provided server lobby.
   *
   * @param gameLobby The new game lobby.
   */
  public void updateGameLobby(GameLobby gameLobby);

}
