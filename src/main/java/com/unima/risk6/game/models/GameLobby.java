package com.unima.risk6.game.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Game lobby model. Contains all the information about a game lobby. A game lobby is a lobby where
 * players can join to play a game.
 *
 * @author astoyano
 */
public class GameLobby {

  private final String lobbyName;
  private String name;


  private int maxPlayers;

  private final boolean isChatEnabled;

  private final int matchMakingElo;

  private UserDto lobbyOwner;

  private final List<UserDto> users;

  private final List<String> bots;

  /**
   * Constructor for a game lobby. Creates a new game lobby with the given name, max players, host
   * name, chat enabled, match making elo and lobby owner.
   *
   * @param lobbyName      The name of the lobby.
   * @param maxPlayers     The max players of the lobby.
   * @param hostName       The name of the host.
   * @param isChatEnabled  If the chat is enabled.
   * @param matchMakingElo The matchmaking elo.
   * @param lobbyOwner     The lobby owner.
   */
  public GameLobby(String lobbyName, int maxPlayers, String hostName, boolean isChatEnabled,
      int matchMakingElo, UserDto lobbyOwner) {
    this.lobbyName = lobbyName;
    this.maxPlayers = maxPlayers;
    this.name = hostName;
    this.isChatEnabled = isChatEnabled;
    this.matchMakingElo = matchMakingElo;
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
    return "GameLobby{" + "lobbyName='" + lobbyName + '\'' + ", name='" + name + '\''
        + ", maxPlayers=" + maxPlayers + ", isChatEnabled=" + isChatEnabled + ", matchMakingElo="
        + matchMakingElo + ", lobbyOwner=" + lobbyOwner + ", users=" + users + ", bots=" + bots
        + '}';
  }
}
