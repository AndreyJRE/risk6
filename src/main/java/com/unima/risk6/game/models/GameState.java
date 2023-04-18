package com.unima.risk6.game.models;

import com.unima.risk6.game.logic.Move;
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

  private final Queue<Player> activePlayers;
  private final ArrayList<Player> lostPlayers;
  private final Set<Country> countries;
  private final Set<Continent> continents;
  private Player currentPlayer;
  private int numberOfHandIns;
  private final ArrayList<Move> lastMoves;
  private final Deck deck;

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
    this.numberOfHandIns = 0;
    this.lastMoves = new ArrayList<>();
    this.currentPlayer = activePlayers.peek();
    this.lostPlayers = new ArrayList<>();
    this.deck = new Deck();


  }

  /**
   * Returns the set of countries in the game.
   *
   * @return the set of countries in the game
   */
  public Set<Country> getCountries() {
    return countries;
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

  public Deck getDeck() {
    return deck;
  }
}

