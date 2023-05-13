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

  public static void startGameServer() {
    gameServer = new GameServer();
    serverLobby = new ServerLobby("Multiplayer server");
    gameServerThread = new Thread(gameServer);
    gameServerThread.start();


  }

  public static void startSinglePlayerServer() {
    gameServer = new GameServer("127.0.0.1");
    serverLobby = new ServerLobby("Single player server");
    gameServerThread = new Thread(gameServer);
    gameServerThread.start();
  }

  public static void stopGameServer() {
    serverLobby = null;
    gameServerThread.interrupt();
  }

  public static ServerLobby getServerLobby() {
    return serverLobby;
  }

  public static Thread getGameServerThread() {
    return gameServerThread;
  }

  public static GameServer getGameServer() {
    return gameServer;
  }
}
