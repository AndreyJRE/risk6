package com.unima.risk6.network.client;

import com.unima.risk6.network.server.GameServer;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ManualServerClientTest {

  public static void main(String[] args) {
    try {
      GameClient gameClient = new GameClient("ws://localhost:8080/game");
      GameServer gameServer = new GameServer();

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