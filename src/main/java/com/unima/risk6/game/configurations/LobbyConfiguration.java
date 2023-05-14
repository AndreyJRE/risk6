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

/**
 * Configuration class for the lobbies in the game. This class is used to store the lobbies and
 * notify observers when the lobbies are updated. This class is also used to store the messages that
 * are sent in the chat. Also, to send messages to the server.
 *
 * @author astoyano
 */
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

  /**
   * Send a message to the server to join the game lobby.
   *
   * @param userDto The user that wants to join the game lobby.
   */
  public static void sendJoinServer(UserDto userDto) {
    gameClient.sendMessage(new ConnectionMessage<>(ConnectionActions.JOIN_SERVER_LOBBY, userDto));

  }

  /**
   * Sends a message to the server to quit the game lobby.
   *
   * @param myGameUser The user that wants to quit the game lobby.
   */
  public static void sendQuitGameLobby(UserDto myGameUser) {
    gameClient.sendMessage(new ConnectionMessage<>(ConnectionActions.LEAVE_GAME_LOBBY, myGameUser));
    gameLobby = null;

  }

  /**
   * Sends a message to the server to quit the server lobby.
   *
   * @param myGameUser The user that wants to quit the server lobby.
   */
  public static void sendQuitServerLobby(UserDto myGameUser) {
    gameClient.sendMessage(
        new ConnectionMessage<>(ConnectionActions.LEAVE_SERVER_LOBBY, myGameUser));

  }

  /**
   * Sends a message to the server to create a game lobby.
   *
   * @param gameLobby The game lobby that the user wants to create.
   */
  public static void sendCreateLobby(GameLobby gameLobby) {
    gameClient.sendMessage(new ConnectionMessage<>(ConnectionActions.CREATE_GAME_LOBBY, gameLobby));
  }

  /**
   * Sends a message to the server to join a game lobby.
   *
   * @param gameLobby The game lobby that the user wants to join.
   */
  public static void sendJoinLobby(GameLobby gameLobby) {
    gameClient.sendMessage(new ConnectionMessage<>(ConnectionActions.JOIN_GAME_LOBBY, gameLobby));
  }

  /**
   * Sends a message to the server to start the game.
   *
   * @param gameLobby The game lobby that the user wants to start.
   */
  public static void sendStartGame(GameLobby gameLobby) {
    gameClient.sendMessage(new ConnectionMessage<>(ConnectionActions.START_GAME, gameLobby));
  }

  /**
   * Sends a message to the server to send a chat message.
   *
   * @param string The message that the user wants to send.
   */
  public static void sendChatMessage(String string) {
    gameClient.sendMessage(new ChatMessage(string));
  }

  /**
   * This method is used to set a last chat message.
   *
   * @param string The message that the user wants to save.
   */
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

  /**
   * This method is used to start the game client. It creates a new thread and starts the game
   * client in that thread.
   */
  public static void startGameClient() {
    gameClientThread = new Thread(gameClient);
    setGameClient(gameClient);
    gameClientThread.start();

  }

  /**
   * This method is used to stop the game client.
   */
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

  /**
   * This method is used to send a message to the server to join a bot game lobby.
   *
   * @param gameLobby The game lobby that the user wants bot to join.
   */
  public static void sendBotJoinLobby(GameLobby gameLobby) {
    gameClient.sendMessage(
        new ConnectionMessage<>(ConnectionActions.JOIN_BOT_GAME_LOBBY, gameLobby));
  }

  /**
   * This method is used to send a message to the server to remove a bot from a game lobby.
   *
   * @param gameLobby The game lobby that the user wants bot to leave.
   */
  public static void sendRemoveBotFromLobby(GameLobby gameLobby) {
    gameClient.sendMessage(
        new ConnectionMessage<>(ConnectionActions.REMOVE_BOT_FROM_LOBBY, gameLobby));
  }

  /**
   * This method is used to send a message to the server to start a tutorial.
   *
   * @param gameLobby The game lobby that the user wants to start the tutorial.
   */
  public static void sendStartTutorial(GameLobby gameLobby) {
    gameClient.sendMessage(new ConnectionMessage<>(ConnectionActions.START_TUTORIAL, gameLobby));
  }

  /**
   * This method is used to send a message to the server to create a tutorial lobby.
   *
   * @param gameLobby The game lobby that the user wants to create the tutorial.
   */
  public static void sendTutorialCreateLobby(GameLobby gameLobby) {
    gameClient.sendMessage(
        new ConnectionMessage<>(ConnectionActions.CREATE_TUTORIAL_LOBBY, gameLobby));
  }
}
