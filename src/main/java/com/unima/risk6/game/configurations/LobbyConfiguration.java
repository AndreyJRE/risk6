package com.unima.risk6.game.configurations;

import com.unima.risk6.game.configurations.observers.GameLobbyObserver;
import com.unima.risk6.game.configurations.observers.ServerLobbyObserver;
import com.unima.risk6.game.logic.HandIn;
import com.unima.risk6.game.models.GameLobby;
import com.unima.risk6.game.models.ServerLobby;
import com.unima.risk6.game.models.UserDto;
import com.unima.risk6.network.client.GameClient;
import com.unima.risk6.network.message.ConnectionActions;
import com.unima.risk6.network.message.ConnectionMessage;
import com.unima.risk6.network.message.StandardMessage;
import java.util.ArrayList;
import java.util.List;

public class LobbyConfiguration {

  private static ServerLobby serverLobby;

  private static GameLobby gameLobby;

  private static final List<ServerLobbyObserver> SERVER_LOBBY_OBSERVERS = new ArrayList<>();

  private static final List<GameLobbyObserver> GAME_LOBBY_OBSERVERS = new ArrayList<>();
  private static GameClient gameClient;
  private static Thread gameClientThread;

  public static ServerLobby getServerLobby() {
    return serverLobby;
  }

  public static void setServerLobby(ServerLobby serverLobby) {
    LobbyConfiguration.serverLobby = serverLobby;
    notifyServerLobbyObservers();
  }

  public static void addServerLobbyObserver(ServerLobbyObserver observer) {
    SERVER_LOBBY_OBSERVERS.add(observer);
  }

  private static void notifyServerLobbyObservers() {
    SERVER_LOBBY_OBSERVERS.forEach(observer -> observer.updateServerLobby(serverLobby));
  }

  public static void addGameLobbyObserver(GameLobbyObserver observer) {
    GAME_LOBBY_OBSERVERS.add(observer);
  }

  private static void notifyGameLobbyObservers() {
    GAME_LOBBY_OBSERVERS.forEach(observer -> observer.updateGameLobby(gameLobby));
  }


  public static void setGameClient(GameClient gameClient) {
    LobbyConfiguration.gameClient = gameClient;
  }

  public static void sendJoinServer(UserDto userDto) {
    //TODO After testing
    /*gameClient.sendMessage(
        new ConnectionMessage<UserDto>(ConnectionActions.JOIN_SERVER_LOBBY, userDto));*/
    gameClient.sendMessage(new StandardMessage<HandIn>(new HandIn(List.of())));

  }

  public static void sendQuitGameLobby(UserDto myGameUser) {
    gameClient.sendMessage(new ConnectionMessage<>(ConnectionActions.LEAVE_GAME_LOBBY, myGameUser));
    gameLobby = null;

  }

  public static void sendCreateLobby(GameLobby gameLobby) {
    gameClient.sendMessage(
        new ConnectionMessage<>(ConnectionActions.CREATE_GAME, gameLobby));
  }

  public static void sendJoinLobby(GameLobby gameLobby) {
    gameClient.sendMessage(
        new ConnectionMessage<>(ConnectionActions.JOIN_GAME_LOBBY, gameLobby));
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

  public static GameLobby getGameLobby() {
    return gameLobby;
  }

  public static void setGameLobby(GameLobby gameLobby) {
    LobbyConfiguration.gameLobby = gameLobby;
  }
}
