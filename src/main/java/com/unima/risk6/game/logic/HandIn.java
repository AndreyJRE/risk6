package com.unima.risk6.game.logic;

import com.unima.risk6.game.models.Card;
import java.util.List;

public class HandIn extends Move {

  private final List<Card> cards;

  public HandIn(List<Card> cards) {
    this.cards = cards;
  }

  public List<Card> getCards() {
    return cards;
  }
}
