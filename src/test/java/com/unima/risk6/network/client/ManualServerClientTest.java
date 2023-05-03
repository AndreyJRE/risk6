package com.unima.risk6.network.client;

import com.unima.risk6.game.configurations.GameConfiguration;
import com.unima.risk6.game.logic.controllers.DeckController;
import com.unima.risk6.game.logic.controllers.GameController;
import com.unima.risk6.game.logic.controllers.PlayerController;
import com.unima.risk6.game.models.Deck;
import com.unima.risk6.network.server.GameServer;
import com.unima.risk6.network.server.MoveProcessor;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ManualServerClientTest {

  public static void main(String[] args) {
    try {
      GameClient gameClient = new GameClient("ws://localhost:8080/game");
      GameServer gameServer = new GameServer(
          new MoveProcessor(new PlayerController(), new GameController(
              GameConfiguration.configureGame(new ArrayList<>(), new ArrayList<>())),
              new DeckController(new Deck())));

      Thread server = new Thread(gameServer);
      server.start();
      Thread.sleep(1000);
      Thread client = new Thread(gameClient);
      client.start();
      Thread.sleep(2000);
      BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
      //gameClient.sendMessage("testest");
      while (true) {
        String msg = console.readLine();
        if (msg == null) {
          break;
        } else {
          System.out.println(msg);
          //gameClient.sendMessage(msg);
          Thread.sleep(1000);
        }
        Thread.sleep(100);
      }
    } catch (Exception e) {

    }

  }
}