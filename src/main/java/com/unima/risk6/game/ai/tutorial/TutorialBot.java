package com.unima.risk6.game.ai.tutorial;

import com.unima.risk6.game.ai.AiBot;
import com.unima.risk6.game.ai.models.MoveTriplet;
import com.unima.risk6.game.logic.Reinforce;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * The bot used in tutorial mode - all the moves it makes are deterministic
 * @author eameri
 */
public class TutorialBot implements AiBot {
  private final Queue<Reinforce> deterministicClaims;
  private final Queue<MoveTriplet> deterministicMoves;

  public TutorialBot() {
    this.deterministicClaims = this.createAllClaims();
    this.deterministicMoves = this.createAllMoves();
  }

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

  private Queue<MoveTriplet> createAllMoves() {
    return null;
  }

  private Queue<Reinforce> createAllClaims() {
    return null;
  }
}
