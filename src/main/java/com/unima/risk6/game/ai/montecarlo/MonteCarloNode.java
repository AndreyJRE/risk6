package com.unima.risk6.game.ai.montecarlo;

import com.unima.risk6.game.ai.bots.MonteCarloBot;
import com.unima.risk6.game.ai.models.MoveTriplet;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.Player;
import java.util.ArrayList;
import java.util.List;

public class MonteCarloNode {

  private static final double EXPLORATION_PARAMETER = Math.sqrt(2);
  private final GameState gameState;
  private int wins;
  private int visits;
  private int depth;
  private final MoveTriplet move;
  private final List<MonteCarloNode> children;
  private MonteCarloNode parent;

  private MonteCarloBot playerInstance;
  private List<MoveTriplet> movesFromHere;

  public MonteCarloBot getPlayerInstance() {
    return playerInstance;
  }

  public MonteCarloNode(GameState gameState, MoveTriplet move, Player playerAtState) {
    this.gameState = gameState;
    this.move = move;
    this.wins = 0;
    this.visits = 0;
    this.children = new ArrayList<>();
    this.parent = null;
    this.depth = 0;
    this.playerInstance = new MonteCarloBot(playerAtState);
    this.movesFromHere = this.playerInstance.getLegalMoves();
  }

  public MonteCarloNode(GameState game, MoveTriplet move, Player playerAtState,
      MonteCarloNode parent) {
    this(game, move, playerAtState);
    this.parent = parent;
    this.depth = parent.getDepth() + 1;
  }

  public void addChild(MonteCarloNode child) {
    this.children.add(child);
  }

  public List<MonteCarloNode> getChildren() {
    return children;
  }

  public GameState getGameState() {
    return gameState;
  }

  public MoveTriplet getMove() {
    return move;
  }

  public int getWins() {
    return wins;
  }

  public int getDepth() {
    return depth;
  }

  public void incrementWins() {
    this.wins++;
  }

  public int getVisits() {
    return visits;
  }

  public void incrementVisits() {
    this.visits++;
  }

  public MonteCarloNode getParent() {
    return parent;
  }


  public boolean isFullyExpanded() {
    return this.movesFromHere.size() == children.size();
  }

  public MonteCarloNode getBestChild() {
    MonteCarloNode bestChild = null;
    double maxUCTValue = Double.NEGATIVE_INFINITY;

    for (MonteCarloNode child : children) {
      double uctValue = calculateUCTValue(child);
      if (uctValue > maxUCTValue) {
        maxUCTValue = uctValue;
        bestChild = child;
      }
    }

    return bestChild;
  }

  private double calculateUCTValue(MonteCarloNode child) {
    return (double) child.getWins() / child.getVisits() + EXPLORATION_PARAMETER * Math.sqrt(
        Math.log(this.getVisits()) / child.getVisits());
  }

  public boolean playerLost() {
    return this.playerInstance == null || this.getGameState().getActivePlayers()
        .contains(playerInstance);
  }
}
