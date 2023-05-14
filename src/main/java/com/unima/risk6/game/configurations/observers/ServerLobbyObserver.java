package com.unima.risk6.game.configurations.observers;

import com.unima.risk6.game.models.ServerLobby;

/**
 * The ServerLobbyObserver defines a method that allows an observer to update the server lobby when
 * the client receives a new server lobby.
 *
 * @author astoyanov
 */
public interface ServerLobbyObserver {

  /**
   * Updates the local server lobby based on the provided server lobby.
   *
   * @param serverLobby The new server lobby.
   */
  void updateServerLobby(ServerLobby serverLobby);

}
