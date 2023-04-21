package com.unima.risk6.game.ai.bots;

import com.unima.risk6.game.ai.AiBot;
import com.unima.risk6.game.ai.models.MoveTriplet;
import com.unima.risk6.game.logic.Reinforce;
import com.unima.risk6.game.logic.controllers.PlayerController;
import com.unima.risk6.game.models.Player;

public class HardBot extends Player implements AiBot {

  private final PlayerController playerController;

  public HardBot() {
    this.playerController = new PlayerController();
    playerController.setPlayer(this);
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
}
