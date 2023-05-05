package com.unima.risk6.network.configurations;

import com.unima.risk6.game.models.ServerLobby;
import com.unima.risk6.network.server.GameServer;
import com.unima.risk6.network.server.MoveProcessor;
import java.net.Inet4Address;
import java.net.UnknownHostException;

/**
 * This class is used to configure the network.
 *
 * @author astoyano
 */
public class NetworkConfiguration {

  private static Thread gameServerThread;
  private static ServerLobby serverLobby;

  public static void startGameServer() {
    GameServer gameServer = new GameServer(new MoveProcessor());
    serverLobby = new ServerLobby("Andrey's server");
    gameServerThread = new Thread(gameServer);
    try {
      System.out.println(Inet4Address.getLocalHost());
    } catch (UnknownHostException e) {
      throw new RuntimeException(e);
    }
    gameServerThread.start();

  }

  public static void stopGameServer() {
    serverLobby = null;
    gameServerThread.interrupt();
  }

  public static ServerLobby getServerLobby() {
    return serverLobby;
  }
}
