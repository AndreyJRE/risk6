package com.unima.risk6.game.ai.bots;

import com.unima.risk6.game.ai.AiBot;
import com.unima.risk6.game.ai.models.CountryPair;
import com.unima.risk6.game.ai.models.MoveTriplet;
import com.unima.risk6.game.ai.montecarlo.MonteCarloTreeSearch;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.logic.Reinforce;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.Player;
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
  private Fortify fortify;
  private GameState gameState;
  private boolean firstAttack = true;

  /**
   * Constructs a new HardBot as a copy of a player.
   *
   * @param player The player to be copied.
   */
  public HardBot(Player player) {
    super(player);
  }

  /**
   * Constructs a default HardBot with a specified username.
   *
   * @param username The username of the HardBot.
   */
  public HardBot(String username) {
    super(username);
  }

  /**
   * Constructs a default HardBot with a randomized name.
   */
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
    if (firstAttack) {
      updateBestMoves();
      firstAttack = false;
    }
    CountryPair updated = null;
    while (!attacks.isEmpty()) {
      CountryPair toUpdate = this.attacks.poll();
      Country attacker = getNewCountryReference(toUpdate.getOutgoing());
      Country defender = getNewCountryReference(toUpdate.getIncoming());
      if (attacker.getPlayer().equals(this) && !defender.getPlayer().equals(this)
          && attacker.getTroops() >= 2) { // check if everything is still alright
        updated = new CountryPair(attacker, defender);
        break;
      }
    }
    return updated;
  }

  @Override
  public Fortify createFortify() { // check if fortify conditions need to be validated
    updateBestMoves();
    firstAttack = true;
    return this.fortify;
  }

  /**
   * Uses the Monte Carlo Tree Search algorithm to update the variables holding the best moves from
   * the current state of the game.
   */
  private void updateBestMoves() {
    MonteCarloTreeSearch mcts = new MonteCarloTreeSearch(this);
    MoveTriplet results = mcts.getBestMove(this.gameState);
    this.reinforces = results.reinforcements();
    this.attacks = results.attacks();
    this.fortify = results.fortify();
  }

  /**
   * Update the value of gameState.
   *
   * @param gameState The state of the game right at the beginning of the HardBots turn.
   */
  public void setGameState(GameState gameState) {
    super.setGameState(gameState);
    this.gameState = gameState;
  }

  @Override
  public boolean attackAgain() { // the hard bot will return all attacks at once
    return !this.attacks.isEmpty();
  }

  private Country getNewCountryReference(Country country) {
    return this.gameState.getCountries().stream().filter(c -> c.equals(country)).findFirst()
        .orElse(null);
  }
}
