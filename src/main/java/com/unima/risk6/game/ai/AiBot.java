package com.unima.risk6.game.ai;

import com.unima.risk6.game.ai.models.MoveTriplet;

/***
 * @author eameri
 */
public interface AiBot {

  /**
   * A method for a bot to make moves for all 3 phases of the game
   */
  MoveTriplet makeMove();

  /**
   * A method for a bot to claim a single country during the CLAIM PHASE Game State.
   */
  void claimCountry();
}
