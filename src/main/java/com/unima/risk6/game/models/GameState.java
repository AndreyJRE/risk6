package com.unima.risk6.game.models;

import com.unima.risk6.game.logic.Move;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

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
  private Queue<Move> lastMoves;
  private final Deck deck;
  private boolean isGameOver;
  private boolean chatEnabled;


  /**
   * Constructs a new game state with the given countries, continents, and players.
   *
   * @param countries     the set of countries in the game
   * @param continents    the set of continents in the game
   * @param activePlayers the queue of players in the game
   */
  public GameState(Set<Country> countries, Set<Continent> continents, Queue<Player> activePlayers) {
    this.countries = countries;
    this.continents = continents;
    this.activePlayers = activePlayers;
    this.numberOfHandIns = 0;
    this.currentPlayer = activePlayers.peek();
    this.lostPlayers = new ArrayList<>();
    this.deck = new Deck();
    this.isGameOver = false;
    this.lastMoves = new ConcurrentLinkedQueue<>();


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

  public Queue<Player> getActivePlayers() {
    return activePlayers;
  }

  public void setNumberOfHandIns(int numberOfHandIns) {
    this.numberOfHandIns = numberOfHandIns;
  }

  public int getNumberOfHandIns() {
    return numberOfHandIns;
  }

  public Set<Continent> getContinents() {
    return continents;
  }


  public void setCurrentPlayer(Player currentPlayer) {
    this.currentPlayer = currentPlayer;
  }

  public Queue<Move> getLastMoves() {
    return lastMoves;
  }


  public ArrayList<Player> getLostPlayers() {
    return lostPlayers;
  }

  public Deck getDeck() {
    return deck;
  }

  public boolean isGameOver() {
    return isGameOver;
  }

  public void setGameOver(boolean gameOver) {
    isGameOver = gameOver;
  }


  public boolean isChatEnabled() {
    return chatEnabled;
  }

  public void setChatEnabled(boolean chatEnabled) {
    this.chatEnabled = chatEnabled;
  }
}


