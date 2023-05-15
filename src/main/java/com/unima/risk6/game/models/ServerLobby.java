package com.unima.risk6.game.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Server lobby model. Contains all the information about a server lobby.
 *
 * @author astoyano
 */
public class ServerLobby {

  private final String lobbyName;
  private final String hostName;
  private final List<GameLobby> gameLobbies;

  private final List<UserDto> users;

  /**
   * Constructor for a server lobby. Creates a new server lobby with the given name and host.
   *
   * @param lobbyName The name of the lobby.
   * @param hostName  The name of the host.
   */
  public ServerLobby(String lobbyName,
      String hostName) {
    this.lobbyName = lobbyName;
    this.hostName = hostName;
    this.gameLobbies = new ArrayList<>();
    this.users = new ArrayList<>();
  }

  /**
   * Constructor for a server lobby. Creates a new server lobby with the given host.
   *
   * @param hostName The name of the host.
   */
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
    return "ServerLobby{" + "lobbyName='" + lobbyName + '\'' + ", hostName='" + hostName + '\''
        + ", gameLobbies="
        + gameLobbies + ", users=" + users + '}';
  }
}
