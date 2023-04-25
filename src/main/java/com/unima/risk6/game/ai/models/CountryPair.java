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
  // TODO: fix javadocs of all methods changed by this class

  private final Country outgoing;
  private final Country incoming;

  public CountryPair(Country outgoing, Country incoming) {
    this.outgoing = outgoing;
    this.incoming = incoming;
  }

  public Country getOutgoing() {
    return outgoing;
  }

  public Country getIncoming() {
    return incoming;
  }

  public Attack createAttack(int troopNumber) {
    return new Attack(this.outgoing, this.incoming, troopNumber);
  }

  public Fortify createFortify(int troopsToMove) {
    return new Fortify(this.outgoing, this.incoming, troopsToMove);
  }

  /**
   * Gets the probability of a country winning an entire battle against another country
   *
   * @return The probability of the attacking country winning the entire battle
   */
  public int getWinningProbability() {
    int attackerCount = getOutgoing().getTroops();
    int defenderCount = getIncoming().getTroops();
    return Probabilities.getWinProbability(attackerCount, defenderCount);
  }

  //TODO: compare method? see usages of sort in mediumbot
}
