package com.unima.risk6.game.logic;

import com.unima.risk6.game.models.Continent;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.game.models.enums.GamePhase;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Set;

public class GameState {

  private Queue<Player> activePlayers;
  private ArrayList<Player> lostPlayers;
  private Set<Country> countries;
  private Set<Continent> continents;
  private Player currentPlayer;
  private Dice dice;
  private int numberOfHandIns;
  private GamePhase currentPhase;

  public GameState(Set<Country> countries
      , Set<Continent> continents
      , Queue<Player> allPlayer) {
    this.countries = countries;
    this.continents = continents;
    this.activePlayers = allPlayer;
    this.dice = new Dice();
  }


  public Set<Country> getCountries() {
    return countries;
  }

  public void setPlayerTurn() {

  }

  public void nextTurn() {
    currentPlayer = activePlayers.poll();
  }

  public void endTurn() {

  }

  public Player getCurrentPlayer() {
    return currentPlayer;
  }

  public Queue<Player> getActivePlayers() {
    return activePlayers;
  }

  public void setNumberOfHandIns(int numberOfHandIns) {
    this.numberOfHandIns = numberOfHandIns;
  }

  public int getNumberOfHandIns() {
    return numberOfHandIns;
  }

  public GamePhase getCurrentPhase() {
    return currentPhase;
  }

  public void setCurrentPhase(GamePhase currentPhase) {
    this.currentPhase = currentPhase;
  }

  public Set<Continent> getContinents() {
    return continents;
  }

  public void setCurrentPlayer(Player currentPlayer) {
    this.currentPlayer = currentPlayer;
  }
}
