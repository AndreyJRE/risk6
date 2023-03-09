package com.unima.risk6.game.logic;

import com.unima.risk6.game.models.Country;

public class Fortify extends Move {

  public Country outgoing;
  public Country incoming;
  public int troopsToMove;

  public Fortify(Country pOutgoing, Country pIncoming, int pTroopsToMove) {
    outgoing = pOutgoing;
    incoming = pIncoming;
    troopsToMove = pTroopsToMove;

  }

  public Country getOutgoing() {
    return outgoing;
  }

  public Country getIncoming() {
    return incoming;
  }

  public int getTroopsToMove() {
    return troopsToMove;
  }
}
