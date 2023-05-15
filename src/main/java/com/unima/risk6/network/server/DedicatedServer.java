package com.unima.risk6.network.server;

import com.unima.risk6.network.configurations.NetworkConfiguration;

/**
 * A simple dedicated server.
 *
 * @author jferch
 */
public class DedicatedServer {

  public static void main(String[] args) {
    NetworkConfiguration.startGameServer();
  }

}
