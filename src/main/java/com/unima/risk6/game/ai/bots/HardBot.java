package com.unima.risk6.game.ai.bots;

import com.unima.risk6.game.ai.AiBot;
import com.unima.risk6.game.ai.models.MoveTriplet;
import com.unima.risk6.game.ai.montecarlo.MonteCarloTreeSearch;

public class HardBot extends MediumBot implements AiBot {


  /**
   * A method for a bot to make moves for all 3 phases of the game
   */
  @Override
  public MoveTriplet makeMove() {
    MonteCarloTreeSearch mcts = new MonteCarloTreeSearch(this);
    // TODO: get GameState from somewhere :(
    return mcts.getBestMove(null);
  }

}
