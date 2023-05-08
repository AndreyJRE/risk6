package com.unima.risk6.game.ai;

import com.unima.risk6.game.ai.models.CountryPair;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.logic.Reinforce;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.GameState;
import java.util.List;

/**
 * An interface representing an AI bot that can play in a Risk game.
 *
 * @author eameri
 */
public interface AiBot {

  /**
   * Creates a list of all Reinforce moves the bot will perform.
   *
   * @return A list of Reinforce objects representing all reinforcement moves to be performed.
   */
  List<Reinforce> createAllReinforcements();

  /**
   * Creates a list of all attacks the bot will perform as CountryPair objects which include both
   * countries involved in each attack.
   *
   * @return A list of CountryPair objects representing all attacks.
   */
  CountryPair createAttack();

  /**
   * Creates a Fortify move for a country pair after a successful attack.
   *
   * @param winPair The CountryPair object representing the countries involved in the successful
   *                attack.
   * @return A Fortify object representing the fortification move after the attack.
   */
  Fortify moveAfterAttack(CountryPair winPair);

  /**
   * Creates a Fortify move representing the bot's chosen fortification move.
   *
   * @return A Fortify object representing the chosen fortification move.
   */
  Fortify createFortify();

  /**
   * Claims a single country for the bot during the Claim Phase game state.
   *
   * @return A Reinforce object representing the claimed country with one troop placed on it.
   */
  Reinforce claimCountry();

  /**
   * Returns if the bot chooses to attack another country.
   *
   * @return if the bot chooses to attack another country.
   */
  boolean attackAgain();

  /**
   * Returns the amount of troops the bot wants to attack with.
   *
   * @param attacker the country attacking.
   * @return the amount of troops the bot wants to attack with.
   */
  int getAttackTroops(Country attacker);

  /**
   * Sets the values relevant to the bots decision-making by copying them from the current game
   * state.
   *
   * @param gameState the current state of the game.
   */
  void setGameState(GameState gameState);
}
