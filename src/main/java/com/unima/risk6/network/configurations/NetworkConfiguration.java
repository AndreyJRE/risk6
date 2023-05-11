package com.unima.risk6.network.configurations;

import com.unima.risk6.game.models.ServerLobby;
import com.unima.risk6.network.server.GameServer;
import com.unima.risk6.network.server.MoveProcessor;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.stream.Collectors;

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
    GameServer gameServer = new GameServer();
    serverLobby = new ServerLobby("Andrey's server");
    gameServerThread = new Thread(gameServer);
    gameServerThread.start();
    try {
      NetworkInterface.getNetworkInterfaces().asIterator().forEachRemaining(x -> System.out.println(
          x.inetAddresses().map(y -> y.getHostAddress()).collect(Collectors.toList())));
    } catch (SocketException e) {
      throw new RuntimeException(e);
    }

  }

  public static void startSinglePlayerServer() {
    MoveProcessor moveProcessor = new MoveProcessor();
    GameServer gameServer = new GameServer("127.0.0.1");
    serverLobby = new ServerLobby("Single player server");
    gameServerThread = new Thread(gameServer);
    gameServerThread.start();
    try {
      NetworkInterface.getNetworkInterfaces().asIterator().forEachRemaining(x -> System.out.println(
          x.inetAddresses().map(y -> y.getHostAddress()).collect(Collectors.toList())));
    } catch (SocketException e) {
      throw new RuntimeException(e);
    }

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
