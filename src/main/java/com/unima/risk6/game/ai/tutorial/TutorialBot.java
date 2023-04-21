package com.unima.risk6.game.ai.tutorial;

import com.unima.risk6.game.ai.AiBot;
import com.unima.risk6.game.ai.models.MoveTriplet;
import com.unima.risk6.game.logic.Reinforce;

/**
 * The bot used in tutorial mode - all the moves it makes are deterministic
 * @author eameri
 */
public class TutorialBot implements AiBot {

  /**
   * A method for a bot to make moves for all 3 phases of the game
   */
  @Override
  public MoveTriplet makeMove() {

    return null;
  }

  /**
   * A method for a bot to claim a single country during the CLAIM PHASE Game State.
   */
  @Override
  public Reinforce claimCountry() {
    return null;
  }
}
