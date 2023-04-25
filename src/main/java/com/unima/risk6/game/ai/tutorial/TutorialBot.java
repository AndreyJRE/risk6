package com.unima.risk6.game.ai.tutorial;

import com.unima.risk6.game.ai.AiBot;
import com.unima.risk6.game.ai.models.CountryPair;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.logic.Reinforce;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * The bot used in tutorial mode - all the moves it makes are deterministic
 * @author eameri
 */
public class TutorialBot implements AiBot {
  private final Queue<Reinforce> deterministicClaims;
  private final Queue<Reinforce> deterministicReinforces;
  private final Queue<CountryPair> deterministicAttacks;
  private final Queue<Fortify> deterministicFortifies;
  public TutorialBot() {
    this.deterministicClaims = this.createAllClaims();
    this.deterministicReinforces = new LinkedList<>();
    this.deterministicAttacks = new LinkedList<>();
    this.deterministicFortifies = new LinkedList<>();
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
   * A method for a bot to claim a single country during the CLAIM PHASE Game State.
   */
  @Override
  public Reinforce claimCountry() {
    return null;
  }

  private Queue<Reinforce> createAllClaims() {
    return null;
  }
}
