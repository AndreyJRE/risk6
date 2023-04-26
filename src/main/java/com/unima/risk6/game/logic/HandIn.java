package com.unima.risk6.game.logic;

import com.unima.risk6.game.models.Card;
import java.util.ArrayList;

public class HandIn extends Move {

  private final ArrayList<Card> cards;

  public HandIn(ArrayList<Card> cards) {
    this.cards = cards;
  }

  public ArrayList<Card> getCards() {
    return cards;
  }
}
