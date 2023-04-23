package com.unima.risk6.game.ai.montecarlo;

import com.google.gson.Gson;
import com.unima.risk6.game.ai.AiBot;
import com.unima.risk6.game.ai.bots.EasyBot;
import com.unima.risk6.game.ai.bots.HardBot;
import com.unima.risk6.game.ai.bots.MediumBot;
import com.unima.risk6.game.ai.bots.MonteCarloBot;
import com.unima.risk6.game.ai.models.MoveTriplet;
import com.unima.risk6.game.ai.models.Probabilities;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.Player;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MonteCarloTreeSearch {


  private static final int SIMULATION_COUNT = 200;
  private static final int SIMULATION_TIME_LIMIT = 500; // see how much time a human has?
  private static final Random RNG = new Random();
  private static final Gson gson = new Gson();
  private final HardBot player;
  private int treeDepth;

  public MonteCarloTreeSearch(HardBot player) {
    this.player = player;
  }

  public MoveTriplet getBestMove(GameState game) {
    MonteCarloNode root = new MonteCarloNode(game, null, this.player);
    for (int i = 0; i < SIMULATION_COUNT; i++) {
      MonteCarloNode node = select(root);
      if (node == null) {
        break;
      }
      expand(node);
      Player result = simulate(node.getGameState());
      backpropagate(node, result);
    }
    return chooseBestMove(root);
  }

  private MonteCarloNode select(MonteCarloNode node) {
    // goes to specific depth
    // keep traversing non-terminal states, finding a leaf node <-> not expanded fully
    while (!node.getGameState().isGameOver() && !node.playerLost()) {
      if (node.isFullyExpanded()) {
        node = node.getBestChild();
      } else {
        return node;
      }
    }
    return null;
  }

  /**
   * Creates the child nodes of a node, each containing one turn's worth of moves
   *
   * @param node The node in the Monte Carlo Tree whose children are to be created
   */
  private void expand(MonteCarloNode node) { // choose only best moves

    List<MoveTriplet> legalMoves = node.getPlayerInstance().getLegalMoves();
    for (MoveTriplet move : legalMoves) {
      boolean moveExists = false;
      for (MonteCarloNode child : node.getChildren()) {
        if (child.getMove().equals(move)) {
          moveExists = true;
          break;
        }
      }
      if (!moveExists) {
        GameState newGameState = this.copyGameState(node.getGameState());
        // SERVER METHOD APPLY MOVE TO THIS GAMESTATE
//        newGameState.applyMove(move);
        MonteCarloNode child = new MonteCarloNode(newGameState, move,
            getPlayerAtGameState(newGameState), node);
        node.addChild(child);
        if (node.getChildren().size() == legalMoves.size()) {
          break;
        }
      }
    }
  }

  /**
   * Plays the game with everyone making greedy moves
   *
   * @param game the GameState from which the simulation will begin
   * @return the strongest player once the game has stopped being simulated
   */
  private Player simulate(GameState game) { // here: mediumbots that keep playing
    // simulation gets stopped after time period
    GameState simulation = this.copyGameState(game);
    List<MoveTriplet> legalMoves;
    long endTime = System.currentTimeMillis() + SIMULATION_TIME_LIMIT;
    // if bot -> createMove, if hardbot or human -> random move
    while (keepSimulating(endTime, null, simulation)) {
//      MoveTriplet randomMove = legalMoves.get(RNG.nextInt(legalMoves.size()));
//      simulation.applyMove(randomMove);
    }

    return Probabilities.findStrongestPlayer(simulation);
  }

  private boolean keepSimulating(long endTime, Player player, GameState simulation) {
    int movesLeft = 1;
    if (player instanceof MonteCarloBot) {
      movesLeft = ((MonteCarloBot) player).getLegalMoves().size();
    }
    return System.currentTimeMillis() < endTime && movesLeft > 0 && !simulation.isGameOver();
  }

  private void backpropagate(MonteCarloNode node, Player result) {
    while (node != null) {
      node.incrementVisits();
      if (Probabilities.findStrongestPlayer(node.getGameState()).equals(result)) {
        node.incrementWins();
      }
      node = node.getParent();
    }
  }

  private MoveTriplet chooseBestMove(MonteCarloNode node) {
    MonteCarloNode bestChild = null;
    int maxVisits = Integer.MIN_VALUE;
    for (MonteCarloNode child : node.getChildren()) {
      if (child.getVisits() > maxVisits) {
        maxVisits = child.getVisits();
        bestChild = child;
      }
    }
    return bestChild.getMove();
  }

  private GameState copyGameState(GameState gameState) {
    GameState copy = gson.fromJson(gson.toJson(gameState), gameState.getClass());
    // swap players
    Map<String, Player> playerMap = new HashMap<>();
    int queueSize = copy.getActivePlayers().size();
    for (int i = 0; i < queueSize; i++) {
      Player player = copy.getActivePlayers().poll();
      // players/ hardbots may make different choices, assume others do as they usually do
      if (!(player instanceof EasyBot || player instanceof MediumBot)) {
        player = new MonteCarloBot(player);
      }
      playerMap.put(player.getUser(), player);
      copy.getActivePlayers().add(player);
    }
    // make changes in countries and current player
    for (Country country : copy.getCountries()) {
      country.setPlayer(playerMap.get(country.getPlayer().getUser()));
    }
    copy.setCurrentPlayer(playerMap.get(copy.getCurrentPlayer().getUser()));

    return copy;
  }

  private Player getPlayerAtGameState(GameState gameState) {
    for (Player player : gameState.getActivePlayers()) {
      if (player.equals(this.player)) {
        return player;
      }
    }
    return null; // signifies that player is no longer in game
  }

}
