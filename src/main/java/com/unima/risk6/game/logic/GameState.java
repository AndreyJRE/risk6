package com.unima.risk6.game.logic;

import com.unima.risk6.game.models.Continent;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.game.models.enums.GamePhase;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Set;

/**
 * Represents the state of a game of Risk, including the current players, countries, continents, and
 * game phase.
 *
 * @author wphung
 */
public class GameState {

  private Queue<Player> activePlayers;
  private ArrayList<Player> lostPlayers;
  private Set<Country> countries;
  private Set<Continent> continents;
  private Player currentPlayer;
  private Dice dice;
  private int numberOfHandIns;
  private GamePhase currentPhase;
  private ArrayList<Move> lastMoves;

  /**
   * Constructs a new game state with the given countries, continents, and players.
   *
   * @param countries     the set of countries in the game
   * @param continents    the set of continents in the game
   * @param activePlayers the queue of players in the game
   */
  public GameState(Set<Country> countries
      , Set<Continent> continents
      , Queue<Player> activePlayers) {
    this.countries = countries;
    this.continents = continents;
    this.activePlayers = activePlayers;
    this.dice = new Dice();
    this.numberOfHandIns = 0;
    this.lastMoves = new ArrayList<>();
    this.currentPhase = GamePhase.CLAIMPHASE;
    this.currentPlayer = activePlayers.peek();


  }

  /**
   * Returns the set of countries in the game.
   *
   * @return the set of countries in the game
   */
  public Set<Country> getCountries() {
    return countries;
  }

  public void setPlayerTurn() {

  }

  /**
   * Advances the game to the next turn.
   */
  public void nextTurn() {
    currentPlayer = activePlayers.poll();
  }

  public void endTurn() {

  }

  /**
   * Returns the current player.
   *
   * @return the current player
   */
  public Player getCurrentPlayer() {
    return currentPlayer;
  }

  /**
   * Returns the queue of active players.
   *
   * @return the queue of active players
   */
  public Queue<Player> getActivePlayers() {
    return activePlayers;
  }

  /**
   * Increments the number of hand-ins that have occurred since the Game has started.
   */
  public void setNumberOfHandIns() {
    this.numberOfHandIns += 1;
  }

  /**
   * Returns the number of hand-ins that have occurred.
   *
   * @return the number of hand-ins that have occurred
   */
  public int getNumberOfHandIns() {
    return numberOfHandIns;
  }

  /**
   * Returns the current game phase.
   *
   * @return the current game phase
   */
  public GamePhase getCurrentPhase() {
    return currentPhase;
  }

  /**
   * Sets the current game phase.
   *
   * @param currentPhase the current game phase
   */
  public void setCurrentPhase(GamePhase currentPhase) {
    this.currentPhase = currentPhase;
  }

  /**
   * Returns the set of continents in the game.
   *
   * @return the set of continents in the game
   */
  public Set<Continent> getContinents() {
    return continents;
  }

  /**
   * Sets the current player to the given player.
   *
   * @param currentPlayer the current player
   */
  public void setCurrentPlayer(Player currentPlayer) {
    this.currentPlayer = currentPlayer;
  }

  public ArrayList<Move> getLastMoves() {
    return lastMoves;
  }

  public ArrayList<Player> getLostPlayers() {
    return lostPlayers;
  }
}
