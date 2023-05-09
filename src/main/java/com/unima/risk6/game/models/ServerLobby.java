package com.unima.risk6.game.models;

import java.util.ArrayList;
import java.util.List;

public class ServerLobby extends Lobby {

  private final String lobbyName;
  private final String hostName;
  private final List<GameLobby> gameLobbies;

  private final List<UserDto> users;

  public ServerLobby(String lobbyName,
      String hostName) {
    this.lobbyName = lobbyName;
    this.hostName = hostName;
    this.gameLobbies = new ArrayList<>();
    this.users = new ArrayList<>();
  }

  public ServerLobby(String hostName) {
    this.hostName = hostName;
    this.gameLobbies = new ArrayList<>();
    this.users = new ArrayList<>();
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

  public List<UserDto> getUsers() {
    return users;
  }

  @Override
  public String toString() {
    return "ServerLobby{" +
        "lobbyName='" + lobbyName + '\'' +
        ", hostName='" + hostName + '\'' +
        ", gameLobbies=" + gameLobbies +
        ", users=" + users +
        '}';
  }
}
