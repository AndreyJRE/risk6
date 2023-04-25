package com.unima.risk6.game.ai.montecarlo;

import com.unima.risk6.game.ai.bots.EasyBot;
import com.unima.risk6.game.ai.bots.MediumBot;
import com.unima.risk6.game.ai.bots.MonteCarloBot;
import com.unima.risk6.game.ai.models.MoveTriplet;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.Player;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MonteCarloNode {

  private static final double EXPLORATION_PARAMETER = Math.sqrt(2);
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
    // everytime a node is created, it is our players turn
    this.testedMoves = new HashSet<>();
  }

  public MonteCarloNode(GameState game, MoveTriplet move, MonteCarloNode parent) {
    this(game, move);
    this.parent = parent;
    this.parent.addChild(this);
    this.depth = parent.getDepth() + 1;
  }

  public void addChild(MonteCarloNode child) {
    this.children.add(child);
    this.testedMoves.add(child.getMove());
  }

  public List<MonteCarloNode> getChildren() {
    return children;
  }

  public GameState getGameState() {
    return gameState;
  }

  public MoveTriplet getMove() {
    return this.move;
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
    // TODO: find good number for this
    return this.testedMoves.size() > 10;
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

  private double calculateUCTValue(MonteCarloNode child) { // what happens when visits are zero :(
    return (double) child.getWins() / child.getVisits() + EXPLORATION_PARAMETER * Math.sqrt(
        Math.log(this.getVisits()) / child.getVisits());
  }

  public List<MoveTriplet> getLegalMoves(Player player) {
    List<MoveTriplet> legalMoves = new ArrayList<>();
    if (this.playerIsMonteCarlo(player)) {
      legalMoves = ((MonteCarloBot) player).getLegalMoves();
    } else {
//      legalMoves.add(((AiBot) player).makeMove());
    }
    return legalMoves;
  }


  private boolean playerIsMonteCarlo(Player player) {
    return !(player instanceof EasyBot || player instanceof MediumBot);
  }

  public boolean playerLost(Player player) {
    return player == null || this.getGameState().getActivePlayers().contains(player);
  }
}
