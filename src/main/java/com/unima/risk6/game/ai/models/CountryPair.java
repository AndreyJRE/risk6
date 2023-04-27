package com.unima.risk6.game.ai.models;

import com.unima.risk6.game.logic.Attack;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.models.Country;

/**
 * A class to provide a data object for a pair of countries involved in a move
 *
 * @author eameri
 */
public class CountryPair {

  private final Country outgoing;
  private final Country incoming;

  /**
   * Constructs a new CountryPair with the specified outgoing and incoming countries.
   *
   * @param outgoing The outgoing country from which troops will be moved or which will attack.
   * @param incoming The incoming country to which troops will be moved or which will be attacked.
   */
  public CountryPair(Country outgoing, Country incoming) {
    this.outgoing = outgoing;
    this.incoming = incoming;
  }

  /**
   * Returns the outgoing (source) country of the pair.
   *
   * @return The outgoing country.
   */
  public Country getOutgoing() {
    return outgoing;
  }

  /**
   * Returns the incoming (target) country of the pair.
   *
   * @return The incoming country.
   */
  public Country getIncoming() {
    return incoming;
  }

  /**
   * Creates an Attack object for the given pair of countries with the specified number of troops.
   *
   * @param troopNumber The number of troops involved in the attack.
   * @return A new Attack object.
   */
  public Attack createAttack(int troopNumber) {
    return new Attack(this.outgoing, this.incoming, troopNumber);
  }

  /**
   * Creates a Fortify object for the given pair of countries with the specified number of troops to
   * move.
   *
   * @param troopsToMove The number of troops to move from the outgoing to the incoming country.
   * @return A new Fortify object.
   */
  public Fortify createFortify(int troopsToMove) {
    return new Fortify(this.outgoing, this.incoming, troopsToMove);
  }

  /**
   * Gets the probability of the outgoing (attacking) country winning an entire battle against the
   * incoming (defending) country.
   *
   * @return The probability of the attacking country winning the entire battle
   */
  public int getWinningProbability() {
    int attackerCount = getOutgoing().getTroops();
    int defenderCount = getIncoming().getTroops();
    return Probabilities.getWinProbability(attackerCount, defenderCount);
  }
}
