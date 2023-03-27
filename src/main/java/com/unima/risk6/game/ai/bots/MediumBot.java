package com.unima.risk6.game.ai.bots;

import com.unima.risk6.game.ai.AiBot;
import com.unima.risk6.game.logic.GameState;
import com.unima.risk6.game.models.Continent;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.Player;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;

/**
 * The medium difficulty bot, which makes only the best move in each round without analyzing
 * possible future states of the game (greedy).
 * @author eameri
 */
public class MediumBot extends Player implements AiBot {


  private final Random rng;
  private PriorityQueue<Country> weightedCountries;


  public MediumBot() {
    rng = new Random();
  }
  @Override
  public void makeMove(GameState gameState) {
    if (this.numberOfCountries() == 0) { // unable to make a move if bot is out of the game.
      return;
    }
    this.createAllReinforcements();
    this.createAllAttacks();
    this.nextPhase();
    this.createFortify();
    this.nextPhase();
  }
  /**
   * Reinforces based off of weighted country importance
   * @author eameri
   */
  private void createAllReinforcements() {
  }

  private void createAllAttacks() {
  }

  private void createFortify() {
  }

  private PriorityQueue<Country> rateAttacks(Map<Country, List<Country>> allAttacks) {

    for (Country attackingCountry : allAttacks.keySet()) {
      for (Country defendingCountry : allAttacks.get(attackingCountry)) {
        double probability = getWinningProbability(attackingCountry, defendingCountry);

      }
    }
    return null;
  }



  private double getWinningProbability(Country attacker, Country defender) {
    return 0; // will later get value from winning probability matrix
  }
}
