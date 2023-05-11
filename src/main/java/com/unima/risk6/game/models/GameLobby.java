package com.unima.risk6.game.models;

import java.util.ArrayList;
import java.util.List;

public class GameLobby extends Lobby {

  private final String lobbyName;
  private String name;


  private int maxPlayers;

  private final boolean isChatEnabled;

  private final int matchMakingElo;

  private final int phaseTime;

  private UserDto lobbyOwner;

  private final List<UserDto> users;

  private final List<String> bots;


  public GameLobby(String lobbyName, int maxPlayers, String hostName,
      boolean isChatEnabled, int matchMakingElo, int phaseTime, UserDto lobbyOwner) {
    this.lobbyName = lobbyName;
    this.maxPlayers = maxPlayers;
    this.name = hostName;
    this.isChatEnabled = isChatEnabled;
    this.matchMakingElo = matchMakingElo;
    this.phaseTime = phaseTime;
    this.lobbyOwner = lobbyOwner;
    this.users = new ArrayList<>();
    this.bots = new ArrayList<>();
  }

  public String getLobbyName() {
    return lobbyName;
  }

  public int getMaxPlayers() {
    return maxPlayers;
  }

  public void setMaxPlayers(int maxPlayers) {
    this.maxPlayers = maxPlayers;
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

  public int getPhaseTime() {
    return phaseTime;
  }

  public List<UserDto> getUsers() {
    return users;
  }

  public UserDto getLobbyOwner() {
    return lobbyOwner;
  }
  public void setLobbyOwner(UserDto lobbyOwner) {
    this.lobbyOwner = lobbyOwner;
    this.name = lobbyOwner.getUsername();
  }


  public List<String> getBots() {
    return bots;
  }

  @Override
  public String toString() {
    return "GameLobby{" +
        "lobbyName='" + lobbyName + '\'' +
        ", name='" + name + '\'' +
        ", maxPlayers=" + maxPlayers +
        ", isChatEnabled=" + isChatEnabled +
        ", matchMakingElo=" + matchMakingElo +
        ", phaseTime=" + phaseTime +
        ", lobbyOwner=" + lobbyOwner +
        ", users=" + users +
        ", bots=" + bots +
        '}';
  }
}
