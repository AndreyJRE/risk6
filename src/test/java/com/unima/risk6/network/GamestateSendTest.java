package com.unima.risk6.network;


import com.unima.risk6.game.configurations.GameConfiguration;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.logic.Reinforce;
import com.unima.risk6.game.logic.controllers.GameController;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.network.client.GameClient;
import com.unima.risk6.network.configurations.GameClientFactory;
import com.unima.risk6.network.message.StandardMessage;
import com.unima.risk6.network.server.GameServer;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Tests for gamestate sending
 *
 * @author jferch
 */

public class GamestateSendTest {

  private static GameController gamecontroller;
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
      gameClient = GameClientFactory.createGameClient("ws://localhost:42069/game");
      gameServer = new GameServer();
    } catch (Exception e) {
      System.out.println(e.toString());
    }

  }


  @Test
  void testSerializationWithStatus() {
    try {
      //System.out.println(gamestate);
      //System.out.println(Serializer.serialize(new StandardMessage(gamestate, 200)));
      Thread server = new Thread(gameServer);
      server.start();
      Thread.sleep(1000);
      GameClientFactory.startGameClient(gameClient);
      Thread.sleep(4000);
      gameClient.sendMessage(new StandardMessage<>(
          new Reinforce(gameState.getCountries().stream().findAny().get(), 10)));
      Thread.sleep(1000);
      /*gameClient.sendMessage(new StandardMessage<>(
          new Attack(gameState.getCountries().stream().findAny().get(),
              gameState.getCountries().stream().findAny().get(), 10)));
      Thread.sleep(1000);*/
      gameClient.sendMessage(new StandardMessage<>(
          new Fortify(gameState.getCountries().stream().findAny().get(),
              gameState.getCountries().stream().findAny().get(), 10)));
      Thread.sleep(10000);
      //assertEquals("{\"statusCode\":200,\"content\":\"tetest\"}", Serializer.serialize(new StandardMessage("tetest", 200)));
    } catch (Exception e) {
      System.out.println(e.toString());
    }
  }
}
