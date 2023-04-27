package com.unima.risk6.game.ai.bots;

import com.unima.risk6.game.ai.AiBot;
import com.unima.risk6.game.ai.models.CountryPair;
import com.unima.risk6.game.ai.models.MoveTriplet;
import com.unima.risk6.game.ai.montecarlo.MonteCarloTreeSearch;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.logic.Reinforce;
import java.util.List;

public class HardBot extends GreedyBot implements AiBot {


  public HardBot(String username) {
    super(username);
  }

  public HardBot() {
    super();
  }

  @Override
  public List<Reinforce> createAllReinforcements() {
    return null;
  }

  @Override
  public List<CountryPair> createAllAttacks() {
    return null;
  }

  @Override
  public Fortify createFortify() {
    return null;
  }

  /**
   * A method for a bot to make moves for all 3 phases of the game
   */
  public MoveTriplet makeMove() {
    MonteCarloTreeSearch mcts = new MonteCarloTreeSearch(this);
    // TODO: get GameState from somewhere :(
    return mcts.getBestMove(null);
  }

}
