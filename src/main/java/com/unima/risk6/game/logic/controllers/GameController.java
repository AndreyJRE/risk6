package com.unima.risk6.game.logic.controllers;

import static com.unima.risk6.game.models.enums.GamePhase.ATTACK_PHASE;
import static com.unima.risk6.game.models.enums.GamePhase.CLAIM_PHASE;
import static com.unima.risk6.game.models.enums.GamePhase.FORTIFY_PHASE;
import static com.unima.risk6.game.models.enums.GamePhase.NOT_ACTIVE;
import static com.unima.risk6.game.models.enums.GamePhase.REINFORCEMENT_PHASE;

import com.unima.risk6.game.logic.Move;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.game.models.Statistic;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This class represents a management class for the GameState class of the risk game. It manages and
 * performs operations regarding the GameState object it has. It contains methods that are needed
 * for the general game flow of the game.
 *
 * @author wphung
 */

public class GameController {

  private GameState gameState;

  private final Queue<Player> players;

  /**
   * Constructs a new GameController with the given GameState.
   *
   * @param gameState The GameState that is managed by the GameController
   */
  public GameController(GameState gameState) {
    this.gameState = gameState;
    this.players = gameState.getActivePlayers();
  }

  /**
   * Moves on to the next player and updates the game state accordingly.
   */
  public void nextPlayer() {
    Player lastPlayer = players.poll();
    if (players.size() > 0) {
      Player nextPlayer = players.peek();
      gameState.setCurrentPlayer(nextPlayer);
    }
    nextPhase();
    players.add(lastPlayer);
    assert lastPlayer != null;
    lastPlayer.setHasConquered(false);
    if (getCurrentPlayer().getCurrentPhase().equals(REINFORCEMENT_PHASE)) {
      calculateDeployableTroops();
    }
  }

  /**
   * Removes the given player from the active player queue and adds them to the lost player list.
   *
   * @param loser the player who lost the game
   */
  public void removeLostPlayer(Player loser) {
    players.remove(loser);
    gameState.getLostPlayers().add(loser);
    if (!loser.getHand().getCards().isEmpty()) {
      //the Cards of the Players who lost get transferred to the Player who conquered them
      takeOverCardFromLostPlayer(loser);
    }
    if (gameState.getActivePlayers().size() == 1) {
      gameState.setGameOver(true);
    }
  }

  /**
   * Transfers the cards of the lost player to the current player.
   *
   * @param lostPlayer the player who lost the game
   */
  public void takeOverCardFromLostPlayer(Player lostPlayer) {
    gameState.getCurrentPlayer().getHand().getCards().addAll(lostPlayer.getHand().getCards());
    lostPlayer.getHand().getCards().clear();
    lostPlayer.getHand().getSelectedCards().clear();
  }

  /**
   * Sets the active player queue to the given player order.
   *
   * @param playerOrder the new player order
   */
  public void setNewPlayerOrder(Queue<Player> playerOrder) {
    gameState.getActivePlayers().clear();
    playerOrder.forEach(n -> gameState.getActivePlayers().add(n));
    gameState.setCurrentPlayer(gameState.getActivePlayers().peek());
  }

  /**
   * Returns the player order based on the given dice rolls.
   *
   * @param diceRolls a HashMap containing the players and their corresponding dice rolls
   * @return the player order based on the dice rolls
   */
  public Queue<Player> getNewPlayerOrder(HashMap<Player, Integer> diceRolls) {
    Set<Entry<Player, Integer>> entrySet = diceRolls.entrySet();
    Queue<Player> order = new ConcurrentLinkedQueue<>();
    for (int i = 6; i >= 1; i--) {

      for (Entry<Player, Integer> entry : entrySet) {
        if (entry.getValue().equals(i)) {
          order.add(entry.getKey());
        }
      }
    }
    order.forEach(n -> n.setCurrentPhase(NOT_ACTIVE));
    assert order.peek() != null;
    order.peek().setCurrentPhase(CLAIM_PHASE);
    return order;
  }

  /**
   * Adds the given move to the last moves queue in the game state.
   *
   * @param move the last move that was made.
   */
  public void addLastMove(Move move) {
    gameState.getLastMoves().add(move);
  }

  /**
   * Moves on to the next GamePhase of the current player. If the turn of the player has ended after
   * the change of GamePhase the next player is set.
   */
  public void nextPhase() {
    Player player = gameState.getCurrentPlayer();
    switch (player.getCurrentPhase()) {
      case REINFORCEMENT_PHASE -> {
        if (player.getDeployableTroops() == 0) {
          player.setCurrentPhase(ATTACK_PHASE);
        }
      }
      case ATTACK_PHASE -> player.setCurrentPhase(FORTIFY_PHASE);
      case FORTIFY_PHASE, CLAIM_PHASE -> {
        player.setCurrentPhase(NOT_ACTIVE);
        nextPlayer();
      }
      case NOT_ACTIVE -> {
        if (player.getInitialTroops() > 0) {
          player.setCurrentPhase(CLAIM_PHASE);
        } else {
          player.setCurrentPhase(REINFORCEMENT_PHASE);
        }
      }
      default -> {
      }
    }
  }

  /**
   * Calculates and sets the troops the player can deploy according to the occupied countries and
   * continents.
   */

  public void calculateDeployableTroops() {
    Player currentPlayer = gameState.getCurrentPlayer();
    this.updateContinentsOfPlayer(currentPlayer);
    currentPlayer.setDeployableTroops(3);
    int n = currentPlayer.getCountries().size();
    if (n > 8) {
      currentPlayer.setDeployableTroops(Math.floorDiv(n, 3));
    }
    currentPlayer.getContinents().forEach((x) -> currentPlayer.setDeployableTroops(
        currentPlayer.getDeployableTroops() + x.getBonusTroops()));
    //Add the DeployableTroops to the statistic as troopsGained
    Statistic statisticOfCurrentPlayer = currentPlayer.getStatistic();

    statisticOfCurrentPlayer.setTroopsGained(
        statisticOfCurrentPlayer.getTroopsGained() + currentPlayer.getDeployableTroops());

  }

  /**
   * Updates the Set of Continents which is fully occupied by the given player.
   *
   * @param player the player whose continents should be updated
   */
  public void updateContinentsOfPlayer(Player player) {
    player.getContinents().clear();
    gameState.getContinents().forEach((n) -> {
      Set<Country> countries = player.getCountries();
      if (countries.containsAll(n.getCountries())) {
        player.getContinents().add(n);
      }
    });

  }

  /**
   * Updates the Sets of continents of all players. calls updateContinentsOfPlayer for each
   * activePlayer.
   */
  public void updateContinentsForAll() {
    gameState.getActivePlayers().forEach(this::updateContinentsOfPlayer);
  }

  /**
   * Counts the troops of each player and saves it in a HashMap which is then returned.
   *
   * @return HashMap<Player, Integer> which maps each player to their respective number of troops
   * they have control over in the game.
   */
  public HashMap<Player, Integer> countTroops() {
    HashMap<Player, Integer> totalTroopsOfPlayers = new HashMap<>();
    for (Player player : players) {
      int troopNumber = 0;
      for (Country country : player.getCountries()) {
        troopNumber += country.getTroops();
      }
      totalTroopsOfPlayers.put(player, troopNumber);
    }
    return totalTroopsOfPlayers;
  }

  /**
   * Counts the countries of each player and saves it in a HashMap which is then returned.
   *
   * @return HashMap<Player, Integer> which maps each player to their respective number of countries
   * they own in the game.
   */
  public HashMap<Player, Integer> countCountries() {
    HashMap<Player, Integer> numberOfCountries = new HashMap<>();
    for (Player player : players) {
      numberOfCountries.put(player, player.getCountries().size());

    }
    return numberOfCountries;
  }

  /**
   * Returns the player whose turn it is.
   *
   * @return current player of the game state.
   */
  public Player getCurrentPlayer() {
    return gameState.getCurrentPlayer();
  }

  /**
   * Returns the game state that is managed by the game controller.
   *
   * @return the game state that is managed by the game controller.
   */
  public GameState getGameState() {
    return gameState;
  }

  /**
   * Changes the GameState that is managed by the GameController into the given GameState.
   */
  public void setGameState(GameState gameState) {
    this.gameState = gameState;
  }
}