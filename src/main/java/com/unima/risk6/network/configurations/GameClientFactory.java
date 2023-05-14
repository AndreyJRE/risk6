package com.unima.risk6.network.configurations;

import com.unima.risk6.network.client.GameClient;
import java.util.HashMap;

/**
 * GameClientFactory is a factory class that is responsible for creating and managing GameClient
 * instances and their associated threads.
 *
 * @author jferch
 */
public class GameClientFactory {

  public static HashMap<GameClient, Thread> threadMap = new HashMap<>();

  /**
   * Creates a new GameClient connected to the specified URL, and associates it with a new thread.
   *
   * @param url The URL of the server to connect to.
   * @return The created GameClient.
   */
  public static GameClient createGameClient(String url) {
    GameClient g = new GameClient(url);
    Thread t = new Thread(g);
    threadMap.put(g, t);
    return g;
  }

  /**
   * Starts the thread associated with the specified GameClient.
   *
   * @param g The GameClient whose thread should be started.
   */
  public static void startGameClient(GameClient g) {
    Thread t = threadMap.get(g);
    t.start();
  }
}
