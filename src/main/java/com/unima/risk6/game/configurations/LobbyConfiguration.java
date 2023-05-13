package com.unima.risk6.game.configurations;

import com.unima.risk6.game.configurations.observers.ChatObserver;
import com.unima.risk6.game.configurations.observers.GameLobbyObserver;
import com.unima.risk6.game.configurations.observers.ServerLobbyObserver;
import com.unima.risk6.game.models.GameLobby;
import com.unima.risk6.game.models.ServerLobby;
import com.unima.risk6.game.models.UserDto;
import com.unima.risk6.network.client.GameClient;
import com.unima.risk6.network.message.ChatMessage;
import com.unima.risk6.network.message.ConnectionMessage;
import com.unima.risk6.network.message.enums.ConnectionActions;
import java.util.ArrayList;
import java.util.List;

public class LobbyConfiguration {

  private static ServerLobby serverLobby;

  private static GameLobby gameLobby;

  private static final List<String> messages = new ArrayList<>();

  private static final List<ServerLobbyObserver> SERVER_LOBBY_OBSERVERS = new ArrayList<>();

  private static final List<GameLobbyObserver> GAME_LOBBY_OBSERVERS = new ArrayList<>();

  private static final List<ChatObserver> CHAT_OBSERVERS = new ArrayList<>();
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

  public static void addChatObserver(ChatObserver observer) {
    CHAT_OBSERVERS.add(observer);
  }

  private static void notifyChatLobbyObservers() {
    CHAT_OBSERVERS.forEach(observer -> observer.updateChat(messages));
  }


  public static void setGameClient(GameClient gameClient) {
    LobbyConfiguration.gameClient = gameClient;
  }

  public static void sendJoinServer(UserDto userDto) {
    gameClient.sendMessage(
        new ConnectionMessage<>(ConnectionActions.JOIN_SERVER_LOBBY, userDto));

  }

  public static void sendQuitGameLobby(UserDto myGameUser) {
    gameClient.sendMessage(new ConnectionMessage<>(ConnectionActions.LEAVE_GAME_LOBBY, myGameUser));
    gameLobby = null;

  }

  public static void sendQuitServerLobby(UserDto myGameUser) {
    gameClient.sendMessage(
        new ConnectionMessage<>(ConnectionActions.LEAVE_SERVER_LOBBY, myGameUser));

  }

  public static void sendCreateLobby(GameLobby gameLobby) {
    gameClient.sendMessage(
        new ConnectionMessage<>(ConnectionActions.CREATE_GAME_LOBBY, gameLobby));
  }

  public static void sendJoinLobby(GameLobby gameLobby) {
    gameClient.sendMessage(
        new ConnectionMessage<>(ConnectionActions.JOIN_GAME_LOBBY, gameLobby));
  }

  public static void sendStartGame(GameLobby gameLobby) {
    gameClient.sendMessage(
        new ConnectionMessage<>(ConnectionActions.START_GAME, gameLobby));
  }

  public static void sendChatMessage(String string) {
    gameClient.sendMessage(
        new ChatMessage(string));
  }

  public static void setLastChatMessage(String string) {
    messages.add(string);
    System.out.println(messages);
    notifyChatLobbyObservers();
  }

  /**
   * This method is used to configure the network client.
   *
   * @param hostIp Server Host IP
   */
  public static void configureGameClient(String hostIp) {
    String url = "ws://" + hostIp + ":" + "8080" + "/game";
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
    notifyGameLobbyObservers();
  }

  public static void sendBotJoinLobby(GameLobby gameLobby) {
    gameClient.sendMessage(
        new ConnectionMessage<>(ConnectionActions.JOIN_BOT_GAME_LOBBY, gameLobby));
  }

  public static void sendRemoveBotFromLobby(GameLobby gameLobby) {
    gameClient.sendMessage(
        new ConnectionMessage<>(ConnectionActions.REMOVE_BOT_FROM_LOBBY, gameLobby));
  }

  public static void sendStartTutorial(GameLobby gameLobby) {
    gameClient.sendMessage(
        new ConnectionMessage<>(ConnectionActions.START_TUTORIAL, gameLobby));
  }

  public static void sendTutorialCreateLobby(GameLobby gameLobby) {
    gameClient.sendMessage(
        new ConnectionMessage<>(ConnectionActions.CREATE_TUTORIAL_LOBBY, gameLobby));
  }

  public static void sendLeaveGameMessage() {
    gameClient.leaveGame();
  }
}
