package com.unima.risk6.game.logic;

import com.unima.risk6.game.models.Continent;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.Player;

import java.util.Queue;
import java.util.Set;

public class GameState {

  private final Queue<Player> allPlayer;
  private final Set<Country> countries;
  private final Set<Continent> continents;
  private Player currentPlayer;
  private final Dice dice;

  public GameState(Set<Country> countries
      , Set<Continent> continents
      , Queue<Player> allPlayer) {
    this.countries = countries;
    this.continents = continents;
    this.allPlayer = allPlayer;
    this.dice = new Dice();
  }


  public Set<Country> getCountries() {
    return countries;
  }

  public void setPlayerTurn() {

  }

  public void nextTurn() {
    currentPlayer = allPlayer.poll();
  }

  public void endTurn() {

  }

  public Player getCurrentPlayer() {
    return currentPlayer;
  }

  public Set<Continent> getContinents() {
    return continents;
  }

  public void setCurrentPlayer(Player currentPlayer) {
    this.currentPlayer = currentPlayer;
  }
}
