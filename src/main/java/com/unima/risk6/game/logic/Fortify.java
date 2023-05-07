package com.unima.risk6.game.logic;

import com.unima.risk6.game.models.Country;

/**
 * Represents a fortification move in the game of Risk.
 *
 * @author wphung
 */
public class Fortify extends Move {

  private final Country outgoing;
  private final Country incoming;
  private final int troopsToMove;

  /**
   * Constructs a new fortification move with the given outgoing and incoming countries and number
   * of troops to move.
   *
   * @param outgoing     the country from which troops are being moved
   * @param incoming     the country to which troops are being moved
   * @param troopsToMove the number of troops being moved
   */
  public Fortify(Country outgoing, Country incoming, int troopsToMove) {
    this.outgoing = outgoing;
    this.incoming = incoming;
    this.troopsToMove = troopsToMove;
  }

  /**
   * Returns the country from which troops are being moved.
   *
   * @return the country from which troops are being moved
   */
  public Country getOutgoing() {
    return outgoing;
  }

  /**
   * Returns the country to which troops are being moved.
   *
   * @return the country to which troops are being moved
   */
  public Country getIncoming() {
    return incoming;
  }

  /**
   * Returns the number of troops being moved.
   *
   * @return the number of troops being moved
   */
  public int getTroopsToMove() {
    return troopsToMove;
  }

  @Override
  public String toString() {

    return "this is an a Fortify";
  }
}
