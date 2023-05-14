package com.unima.risk6.network.configurations;

import com.unima.risk6.game.models.ServerLobby;
import com.unima.risk6.network.server.GameServer;

/**
 * This class is used to configure the network.
 *
 * @author astoyano
 */
public class NetworkConfiguration {

  private static Thread gameServerThread;
  private static ServerLobby serverLobby;

  private static GameServer gameServer;

  /**
   * Starts a new GameServer for multiplayer games, creates a new server lobby, and initiates the
   * server thread.
   */
  public static void startGameServer() {
    gameServer = new GameServer();
    serverLobby = new ServerLobby("Multiplayer server");
    gameServerThread = new Thread(gameServer);
    gameServerThread.start();
  }

  /**
   * Starts a new GameServer for single player games, creates a new server lobby, and initiates the
   * server thread.
   *
   * @author jferch
   */
  public static void startSinglePlayerServer() {
    gameServer = new GameServer("127.0.0.1");
    serverLobby = new ServerLobby("Single player server");
    gameServerThread = new Thread(gameServer);
    gameServerThread.start();
  }

  /**
   * Stops the currently running GameServer by clearing the server lobby and interrupting the server
   * thread.
   */
  public static void stopGameServer() {
    serverLobby = null;
    gameServerThread.interrupt();
  }

  /**
   * Returns the current ServerLobby.
   *
   * @return The current ServerLobby instance.
   */
  public static ServerLobby getServerLobby() {
    return serverLobby;
  }

  /**
   * Returns the Thread associated with the currently running GameServer.
   *
   * @return The Thread of the running GameServer.
   */
  public static Thread getGameServerThread() {
    return gameServerThread;
  }

  public static GameServer getGameServer() {
    return gameServer;
  }
}
