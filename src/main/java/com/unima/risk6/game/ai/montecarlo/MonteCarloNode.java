package com.unima.risk6.game.ai.montecarlo;

import com.unima.risk6.game.ai.bots.EasyBot;
import com.unima.risk6.game.ai.bots.MediumBot;
import com.unima.risk6.game.ai.models.MoveTriplet;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.Player;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a node in the Monte Carlo Tree for the Monte Carlo Tree Search (MCTS) algorithm used
 * in decision-making by a HardBot in the Risk game.
 *
 * @author eameri
 */
public class MonteCarloNode {

  private static final double EXPLORATION_PARAMETER = Math.sqrt(2);
  private static final int EXPANSION_AMOUNT = 10;
  private final GameState gameState;
  private int wins;
  private int visits;
  private int depth;
  private final MoveTriplet move;
  private final List<MonteCarloNode> children;
  private MonteCarloNode parent;
  private final Set<MoveTriplet> testedMoves;


  public MonteCarloNode(GameState gameState, MoveTriplet move) {
    this.gameState = gameState;
    this.move = move;
    this.wins = 0;
    this.visits = 0;
    this.children = new ArrayList<>();
    this.parent = null;
    this.depth = 0;
    this.testedMoves = new HashSet<>();
  }

  public MonteCarloNode(GameState game, MoveTriplet move, MonteCarloNode parent) {
    this(game, move);
    this.parent = parent;
    this.parent.addChild(this);
    this.depth = parent.getDepth() + 1;
  }

  /**
   * Adds the given child node to the list of children of this node and updates the set of tested
   * moves.
   *
   * @param child The child node to be added.
   */
  public void addChild(MonteCarloNode child) {
    this.children.add(child);
    this.testedMoves.add(child.getMove());
  }

  /**
   * Returns the list of child nodes for this node.
   *
   * @return The list of child nodes.
   */
  public List<MonteCarloNode> getChildren() {
    return children;
  }

  /**
   * Returns the GameState associated with this node.
   *
   * @return The GameState of this node.
   */
  public GameState getGameState() {
    return gameState;
  }

  /**
   * Returns the MoveTriplet associated with this node.
   *
   * @return The MoveTriplet of this node.
   */
  public MoveTriplet getMove() {
    return this.move;
  }

  /**
   * Returns the number of wins of this node.
   *
   * @return The number of wins.
   */
  public int getWins() {
    return wins;
  }

  /**
   * Returns the depth of this node in the Monte Carlo Tree.
   *
   * @return The depth of the node.
   */
  public int getDepth() {
    return depth;
  }

  /**
   * Increments the number of wins for this node.
   */
  public void incrementWins() {
    this.wins++;
  }

  /**
   * Returns the number of visits to this node.
   *
   * @return The number of visits.
   */
  public int getVisits() {
    return visits;
  }

  /**
   * Increments the number of visits to this node.
   */
  public void incrementVisits() {
    this.visits++;
  }

  /**
   * Returns the parent node of this node.
   *
   * @return The parent MonteCarloNode.
   */
  public MonteCarloNode getParent() {
    return parent;
  }

  /**
   * Determines whether this node is fully expanded based on the number of tested moves.
   *
   * @return A boolean value indicating if the node is fully expanded.
   */
  public boolean isFullyExpanded() {
    // TODO: find good number for this
    return this.testedMoves.size() > EXPANSION_AMOUNT;
  }

  /**
   * Returns the best child node according to the UCT (Upper Confidence Bound applied to Trees)
   * value.
   *
   * @return The best child Node.
   */
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

  /**
   * Calculates the UCT (Upper Confidence Bound applied to Trees) value for the given child node,
   * taking into account the number of wins, visits, and exploration parameter.
   *
   * @param child The child node for which the UCT value is to be calculated.
   * @return The calculated UCT value.
   */
  private double calculateUCTValue(MonteCarloNode child) { // what happens when visits are zero :(
    return (double) child.getWins() / child.getVisits() + EXPLORATION_PARAMETER * Math.sqrt(
        Math.log(this.getVisits()) / child.getVisits());
  }


  /**
   * Checks if the given player is an instance of MonteCarloBot.
   *
   * @param player The player to be checked.
   * @return A boolean value indicating whether the player is an instance of MonteCarloBot.
   */
  private boolean playerIsMonteCarlo(Player player) {
    return !(player instanceof EasyBot || player instanceof MediumBot);
  }

  public boolean playerLost(Player player) {
    return player == null || this.getGameState().getActivePlayers().contains(player);
  }
}
