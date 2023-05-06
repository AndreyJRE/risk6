package com.unima.risk6.network.configurations;

import com.unima.risk6.game.configurations.GameConfiguration;
import com.unima.risk6.game.logic.controllers.DeckController;
import com.unima.risk6.game.logic.controllers.GameController;
import com.unima.risk6.game.logic.controllers.PlayerController;
import com.unima.risk6.game.models.Deck;
import com.unima.risk6.game.models.ServerLobby;
import com.unima.risk6.network.server.GameServer;
import com.unima.risk6.network.server.MoveProcessor;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class is used to configure the network.
 *
 * @author astoyano
 */
public class NetworkConfiguration {

  private static Thread gameServerThread;
  private static ServerLobby serverLobby;

  public static void startGameServer() {
    //GameServer gameServer = new GameServer(new MoveProcessor());
    //TODO Gamestate im Server erstellen
    GameConfiguration.setGameState(GameConfiguration.configureGame(
        Arrays.asList("Test"), new ArrayList<>()));
    GameServer gameServer = new GameServer(
        new MoveProcessor(new PlayerController(), new GameController(
            GameConfiguration.getGameState()), new DeckController(new Deck())));
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
