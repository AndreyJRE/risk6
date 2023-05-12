package com.unima.risk6.game.ai.montecarlo;

import com.unima.risk6.game.ai.models.MoveTriplet;
import com.unima.risk6.game.models.GameState;
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
  private static final int EXPANSION_AMOUNT = 15;
  private final GameState gameState;
  private int wins;
  private int visits;
  private final MoveTriplet move;
  private final List<MonteCarloNode> children;
  private MonteCarloNode parent;
  private final Set<MoveTriplet> testedMoves;


  /**
   * Constructs a new node in the MonteCarlo Tree.
   *
   * @param gameState The GameState during creation of the node.
   * @param move      The move used to get to this point.
   */
  public MonteCarloNode(GameState gameState, MoveTriplet move) {
    this.gameState = gameState;
    this.move = move;
    this.wins = 0;
    this.visits = 0;
    this.children = new ArrayList<>();
    this.parent = null;
    this.testedMoves = new HashSet<>();
  }

  /**
   * Constructs a new node in the MonteCarlo Tree and links it to its parent node.
   *
   * @param gameState The GameState during creation of the node.
   * @param move      The move used to get to this point.
   * @param parent    The parent of this node.
   */
  public MonteCarloNode(GameState gameState, MoveTriplet move, MonteCarloNode parent) {
    this(gameState, move);
    this.parent = parent;
    this.parent.addChild(this);
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
  public boolean isFullyExpanded() { // find a good number for this
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
    double maxUctValue = Double.NEGATIVE_INFINITY;

    for (MonteCarloNode child : children) {
      double uctValue = calculateUctValue(child);
      if (uctValue > maxUctValue) {
        maxUctValue = uctValue;
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
  private double calculateUctValue(MonteCarloNode child) {
    return (double) child.getWins() / child.getVisits() + EXPLORATION_PARAMETER * Math.sqrt(
        Math.log(this.getVisits()) / child.getVisits());
  }
}
