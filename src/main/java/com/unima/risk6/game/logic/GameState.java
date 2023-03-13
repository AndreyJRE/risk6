package com.unima.risk6.game.logic;

import com.unima.risk6.game.models.Continent;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.game.models.enums.ContinentName;
import com.unima.risk6.game.models.enums.CountryName;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Stream;

public class GameState {

  private Queue<Player> allPlayer;
  private Set<Country> countries;
  private Set<Continent> continents;
  private Player currentPlayer;
  private Dice dice;

  public GameState() {
    countries = new HashSet<Country>();
    continents = new HashSet<Continent>();
    dice= new Dice();
  }


  public Set<Country> getCountries() {
    return countries;
  }

  public void setPlayerTurn(){

  }
  public void nextTurn(){
    currentPlayer = allPlayer.poll();
  }
  public void endTurn(){

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
