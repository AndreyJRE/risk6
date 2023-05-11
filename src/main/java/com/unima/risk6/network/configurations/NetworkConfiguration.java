package com.unima.risk6.network.configurations;

import com.unima.risk6.game.models.ServerLobby;
import com.unima.risk6.network.server.GameServer;
import com.unima.risk6.network.server.MoveProcessor;

/**
 * This class is used to configure the network.
 *
 * @author astoyano
 */
public class NetworkConfiguration {

  private static Thread gameServerThread;
  private static ServerLobby serverLobby;

  public static void startGameServer() {
    MoveProcessor moveProcessor = new MoveProcessor();
    GameServer gameServer = new GameServer(moveProcessor);
    serverLobby = new ServerLobby("Multiplayer server");
    gameServerThread = new Thread(gameServer);
    gameServerThread.start();


  }

  public static void startSinglePlayerServer() {
    MoveProcessor moveProcessor = new MoveProcessor();
    GameServer gameServer = new GameServer(moveProcessor, "127.0.0.1");
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

}
