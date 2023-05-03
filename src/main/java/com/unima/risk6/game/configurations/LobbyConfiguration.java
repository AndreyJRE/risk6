package com.unima.risk6.game.configurations;

import com.unima.risk6.game.models.ServerLobby;
import java.util.ArrayList;
import java.util.List;

public class LobbyConfiguration {

  private static ServerLobby serverLobby;

  private static final List<ServerLobbyObserver> observers = new ArrayList<>();

  public static ServerLobby getServerLobby() {
    return serverLobby;
  }

  public static void setServerLobby(ServerLobby serverLobby) {
    LobbyConfiguration.serverLobby = serverLobby;
    notifyObservers();
  }

  public static void addObserver(ServerLobbyObserver observer) {
    observers.add(observer);
  }

  private static void notifyObservers() {
    observers.forEach(observer -> observer.updateServerLobby(serverLobby));
  }
}
