package com.unima.risk6.game.logic.controllers;

import static com.unima.risk6.game.models.enums.GamePhase.ATTACK_PHASE;
import static com.unima.risk6.game.models.enums.GamePhase.CLAIM_PHASE;
import static com.unima.risk6.game.models.enums.GamePhase.FORTIFY_PHASE;
import static com.unima.risk6.game.models.enums.GamePhase.NOT_ACTIVE;
import static com.unima.risk6.game.models.enums.GamePhase.REINFORCEMENT_PHASE;

import com.unima.risk6.game.configurations.GameConfiguration;
import com.unima.risk6.game.configurations.GameStateObserver;
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


public class GameController implements GameStateObserver {

  private GameState gameState;

  private final Queue<Player> players;

  public GameController(GameState gameState) {
    this.gameState = gameState;
    this.players = gameState.getActivePlayers();
    GameConfiguration.addObserver(this);
  }

  public void nextPlayer() {
    Player lastPlayer = players.poll();
    Player nextPlayer = players.peek();
    gameState.setCurrentPlayer(nextPlayer);
    nextPhase();
    //if(gameState.getPhase) Wenn es Reinforcementphase ist, soll die Deployable troops berechnet werden
    players.add(lastPlayer);
    assert lastPlayer != null;
    lastPlayer.setHasConquered(false);
    if (getCurrentPlayer().getCurrentPhase().equals(REINFORCEMENT_PHASE)) {
      calculateDeployableTroops();
    }
  }

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

  public void takeOverCardFromLostPlayer(Player lostPlayer) {
    gameState.getCurrentPlayer().getHand().getCards().addAll(lostPlayer.getHand().getCards());
    lostPlayer.getHand().getCards().clear();
    lostPlayer.getHand().getSelectedCards().clear();
  }

  public void setNewPlayerOrder(Queue<Player> playerOrder) {
    gameState.getActivePlayers().clear();
    playerOrder.forEach(n -> gameState.getActivePlayers().add(n));
  }


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


  public void addLastMove(Move move) {
    gameState.setLastMove(move);
  }

  @Override
  public void update(GameState gameState) {
    this.gameState = gameState;
  }

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

  public void updateContinentsOfPlayer(Player player) {
    player.getContinents().clear();
    gameState.getContinents().forEach((n) -> {
      Set<Country> countries = player.getCountries();
      if (countries.containsAll(n.getCountries())) {
        player.getContinents().add(n);
      }
    });

  }

  //Adds all full continents of a Player to the List
  public void updateContinentsForAll() {
    gameState.getActivePlayers().forEach(this::updateContinentsOfPlayer);
  }

  //TODO MOVE TO UI, used by UI
  /*
  public Set<Country> getCountriesToAttackFrom() {
    return gameState.getCurrentPlayer().getCountries().stream().filter(n -> n.getTroops() > 1)
        .collect(
            Collectors.toSet());
  }

  public Set<Country> getOwnedCountries() {
    return gameState.getCurrentPlayer().getCountries();

  }

  public Set<Country> getCountriesToFortifyFrom() {
    return gameState.getCurrentPlayer().getCountries().stream().filter(n -> n.getTroops() > 1)
        .collect(
            Collectors.toSet());
  }

  public Set<Country> getCountriesToFortify(Country country) {
    return country.getAdjacentCountries().stream()
        .filter(n -> n.getPlayer().equals(country.getPlayer())).collect(
            Collectors.toSet());
  }

  public Set<Country> getCountriesToAttack(Country country) {
    return country.getAdjacentCountries().stream()
        .filter(n -> !n.getPlayer().equals(country.getPlayer())).collect(
            Collectors.toSet());

  }

  public Set<Country> getCountriesToClaim() {
    return gameState.getCountries().stream().filter(country -> !country.hasPlayer()).collect(
        Collectors.toSet());
  }
*/
  public Player getCurrentPlayer() {
    return gameState.getCurrentPlayer();
  }

  public GameState getGameState() {
    return gameState;
  }

  public void setGameState(GameState gameState) {
    this.gameState = gameState;
  }
}