package com.unima.risk6.game.configurations;

import com.unima.risk6.game.models.ServerLobby;
import com.unima.risk6.game.models.UserDto;
import com.unima.risk6.network.client.GameClient;
import com.unima.risk6.network.message.ConnectionActions;
import com.unima.risk6.network.message.ConnectionMessage;
import java.util.ArrayList;
import java.util.List;

public class LobbyConfiguration {

  private static ServerLobby serverLobby;

  private static final List<ServerLobbyObserver> observers = new ArrayList<>();
  private static GameClient gameClient;
  private static Thread gameClientThread;

  public static ServerLobby getServerLobby() {
    return serverLobby;
  }

  public static void setServerLobby(ServerLobby serverLobby) {
    LobbyConfiguration.serverLobby = serverLobby;
    notifyObservers();
  }

  public static void addObserver(ServerLobbyObserver observer) {
    observers.add(observer);
  }

  private static void notifyObservers() {
    observers.forEach(observer -> observer.updateServerLobby(serverLobby));
  }

  public static void setGameClient(GameClient gameClient) {
    LobbyConfiguration.gameClient = gameClient;
  }

  public static void sendJoinServer(UserDto userDto) {
    gameClient.sendMessage(
        new ConnectionMessage<UserDto>(ConnectionActions.JOIN_SERVER_LOBBY, userDto));
  }

  /**
   * This method is used to configure the network client.
   *
   * @param hostIp Server Host IP
   * @param port   Server Port
   */
  public static void configureGameClient(String hostIp, int port) {
    String url = "ws://" + hostIp + ":" + port + "/game";
    gameClient = new GameClient(url);
  }

  /**
   * This method is used to get the game client.
   *
   * @return GameClient
   */
  public static GameClient getGameClient() {
    return gameClient;
  }

  public static void startGameClient() {
    gameClientThread = new Thread(gameClient);
    setGameClient(gameClient);
    gameClientThread.start();

  }

  public static void stopGameClient() {
    gameClientThread.interrupt();
  }
}
