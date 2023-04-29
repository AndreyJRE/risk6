package com.unima.risk6.game.models;

import java.util.ArrayList;
import java.util.List;

public class ServerLobby {

  private final String lobbyName;
  private final String hostName;
  private final List<GameLobby> gameLobbies;

  public ServerLobby(String lobbyName,
      String hostName) {
    this.lobbyName = lobbyName;
    this.hostName = hostName;
    this.gameLobbies = new ArrayList<>();
  }

  public ServerLobby(String hostName) {
    this.hostName = hostName;
    this.gameLobbies = new ArrayList<>();
    this.lobbyName = "Risk";
  }

  public String getLobbyName() {
    return lobbyName;
  }

  public String getHostName() {
    return hostName;
  }

  public List<GameLobby> getGameLobbies() {
    return gameLobbies;
  }
}
