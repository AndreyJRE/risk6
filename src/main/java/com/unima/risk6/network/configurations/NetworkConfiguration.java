package com.unima.risk6.network.configurations;

import com.unima.risk6.network.client.GameClient;

/**
 * This class is used to configure the network client.
 *
 * @author astoyano
 */
public class NetworkConfiguration {

  private static GameClient gameClient;

  /**
   * This method is used to configure the network client.
   *
   * @param hostIp Server Host IP
   * @param port   Server Port
   * @return GameClient
   */
  public static GameClient configureGameClient(String hostIp, int port) {
    String url = "ws://" + hostIp + ":" + port + "/game";
    gameClient = new GameClient(url);
    return gameClient;
  }

  /**
   * This method is used to get the game client.
   *
   * @return GameClient
   */
  public static GameClient getGameClient() {
    return gameClient;
  }
}
