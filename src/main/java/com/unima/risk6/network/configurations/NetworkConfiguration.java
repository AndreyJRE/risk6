package com.unima.risk6.network.configurations;

import com.unima.risk6.game.configurations.GameConfiguration;
import com.unima.risk6.game.logic.controllers.DeckController;
import com.unima.risk6.game.logic.controllers.GameController;
import com.unima.risk6.game.logic.controllers.PlayerController;
import com.unima.risk6.game.models.Deck;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.game.models.ServerLobby;
import com.unima.risk6.network.server.GameServer;
import com.unima.risk6.network.server.MoveProcessor;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
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
    //GameServer gameServer = new GameServer(new MoveProcessor());
    //TODO Gamestate im Server erstellen
    List<String> usersList = Arrays.asList("Andrey", "Adolf");
    GameConfiguration.setGameState(GameConfiguration.configureGame(
        usersList, new ArrayList<>()));
    MoveProcessor moveProcessor = new MoveProcessor(new PlayerController(), new GameController(
        GameConfiguration.getGameState()), new DeckController(new Deck()));
    GameServer gameServer = new GameServer(
        moveProcessor);
    //TODO Test gamestate skip order phase
    HashMap<Player, Integer> diceRolls = new HashMap<>();
    diceRolls.put(moveProcessor.getGameController().getGameState().getActivePlayers().poll(), 6);
    diceRolls.put(moveProcessor.getGameController().getGameState().getActivePlayers().poll(), 1);
    moveProcessor.getGameController()
        .setNewPlayerOrder(moveProcessor.getGameController().getNewPlayerOrder(diceRolls));
    Player activePlayer = moveProcessor.getGameController().getGameState().getActivePlayers()
        .peek();
    moveProcessor.getGameController().getGameState().setCurrentPlayer(
        activePlayer);
    moveProcessor.getPlayerController().setPlayer(activePlayer);

    serverLobby = new ServerLobby("Andrey's server");
    gameServerThread = new Thread(gameServer);
    try {
      NetworkInterface.getNetworkInterfaces().asIterator()
          .forEachRemaining(x -> System.out.println(x.inetAddresses().map(
              y -> y.getHostAddress()).collect(Collectors.toList())));
    } catch (SocketException e) {
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

  public static Thread getGameServerThread() {
    return gameServerThread;
  }
}
