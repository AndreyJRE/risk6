package com.unima.risk6.game.ai.bots;

import com.unima.risk6.game.ai.AiBot;
import com.unima.risk6.game.ai.models.CountryPair;
import com.unima.risk6.game.ai.models.MoveTriplet;
import com.unima.risk6.game.ai.montecarlo.MonteCarloTreeSearch;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.logic.Reinforce;
import com.unima.risk6.game.models.GameState;
import java.util.List;
import java.util.Queue;

/**
 * The Hard Bot, which uses the Monte Carlo Tree Search Algorithm to identify its moves during the
 * game, and otherwise makes greedy decisions.
 *
 * @author eameri
 */
public class HardBot extends GreedyBot implements AiBot {

  private List<Reinforce> reinforces;
  private Queue<CountryPair> attacks;
  private Fortify fortifies;

  private GameState currentGameState;


  public HardBot(String username) {
    super(username);
  }

  public HardBot() {
    this("HardBot #" + RNG.nextInt(1000));
  }

  @Override
  public List<Reinforce> createAllReinforcements() {
    updateBestMoves();
    return this.reinforces;
  }

  @Override
  public CountryPair createAttack() {
    return this.attacks.poll();
  }

  @Override
  public Fortify createFortify() {
    // MODIFY FORTIFY TO readjust numbers
    return this.fortifies;
  }

  /**
   * Uses the Monte Carlo Tree Search algorithm to update the variables holding the best moves from
   * the current state of the game.
   */
  private void updateBestMoves() {
    MonteCarloTreeSearch mcts = new MonteCarloTreeSearch(this);
    MoveTriplet results = mcts.getBestMove(this.currentGameState);
    this.reinforces = results.reinforcements();
    this.attacks = results.attacks();
    this.fortifies = results.fortify();
  }

  /**
   * Update the value of currentGameState.
   *
   * @param currentGameState The state of the game right at the beginning of the HardBots turn.
   */
  public void setCurrentGameState(GameState currentGameState) {
    this.currentGameState = currentGameState;
  }

  @Override
  public boolean attackAgain() { // the hard bot will return all attacks at once
    return this.attacks.size() > 0;
  }
}
