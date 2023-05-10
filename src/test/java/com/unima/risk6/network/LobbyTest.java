package com.unima.risk6.network;

import com.unima.risk6.game.configurations.GameConfiguration;
import com.unima.risk6.game.logic.controllers.DeckController;
import com.unima.risk6.game.logic.controllers.GameController;
import com.unima.risk6.game.logic.controllers.PlayerController;
import com.unima.risk6.game.models.Deck;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.ServerLobby;
import com.unima.risk6.network.client.GameClient;
import com.unima.risk6.network.configurations.GameClientFactory;
import com.unima.risk6.network.message.ConnectionMessage;
import com.unima.risk6.network.message.enums.ConnectionActions;
import com.unima.risk6.network.server.GameServer;
import com.unima.risk6.network.server.MoveProcessor;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class LobbyTest {

  private static GameState gameState;
  private static GameClient gameClient;
  private static GameServer gameServer;

  @BeforeAll
  static void setUp() {

    try {
            /*ArrayList<String> users =  new ArrayList<String>(Arrays.asList("Andrey","Max","Fung"));
            ArrayList<AiBot> bots = new ArrayList<AiBot>(Arrays.asList(new EasyBot(),new EasyBot()));
            gamestate = GameConfiguration.configureGame(users, bots);
            gamecontroller = new GameController(gamestate);
             */
      gameState = GameConfiguration.configureGame(new ArrayList<>(), new ArrayList<>());
      gameClient = GameClientFactory.createGameClient("ws://localhost:8080/game");
      gameServer = new GameServer(new MoveProcessor(new PlayerController(),
          new GameController(GameConfiguration.configureGame(
              Arrays.asList("Test"), new ArrayList<>())), new DeckController(new Deck())));
    } catch (Exception e) {
      System.out.println(e.toString());
    }

  }

  @Test
  void testSerializationWithStatus() {
    try {
      Thread server = new Thread(gameServer);
      server.start();
      Thread.sleep(1000);
      GameClientFactory.startGameClient(gameClient);
      Thread.sleep(4000);
      ConnectionMessage connectionMessage = new ConnectionMessage(
          ConnectionActions.JOIN_SERVER_LOBBY, new ServerLobby("lolo"));
      gameClient.sendMessage(connectionMessage);
      Thread.sleep(1000);

      Thread.sleep(10000);
      //assertEquals("{\"statusCode\":200,\"content\":\"tetest\"}", Serializer.serialize(new StandardMessage("tetest", 200)));
    } catch (Exception e) {
      System.out.println(e.toString());
    }
  }


}
