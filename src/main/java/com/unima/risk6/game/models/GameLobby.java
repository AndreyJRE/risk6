package com.unima.risk6.game.models;

import java.util.ArrayList;
import java.util.List;

public class GameLobby {

  private final String lobbyName;
  private final String name;
  private final int maxPlayers;

  private final boolean isChatEnabled;

  private final int matchMakingElo;

  private final int turnTime;

  private final List<UserDto> users;


  public GameLobby(String lobbyName, int maxPlayers, String hostName,
      boolean isChatEnabled, int matchMakingElo, int turnTime) {
    this.lobbyName = lobbyName;
    this.maxPlayers = maxPlayers;
    this.name = hostName;
    this.isChatEnabled = isChatEnabled;
    this.matchMakingElo = matchMakingElo;
    this.turnTime = turnTime;
    this.users = new ArrayList<>();
  }

  public String getLobbyName() {
    return lobbyName;
  }

  public int getMaxPlayers() {
    return maxPlayers;
  }

  public String getName() {
    return name;
  }

  public boolean isChatEnabled() {
    return isChatEnabled;
  }

  public int getMatchMakingElo() {
    return matchMakingElo;
  }

  public int getTurnTime() {
    return turnTime;
  }

  public List<UserDto> getUsers() {
    return users;
  }
}
