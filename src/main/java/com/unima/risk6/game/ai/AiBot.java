package com.unima.risk6.game.ai;

/***
 * @author eameri
 */
public interface AiBot {

  /**
   * A method for a bot to make moves for all 3 phases of the game
   */
  void makeMove(); // gameState unnecessary since we have controllers?

  /**
   * A method for a bot to claim a single country during the CLAIM PHASE Game State.
   */
  void claimCountry();
}
