package com.unima.risk6.game.logic;

import com.unima.risk6.game.models.Card;
import java.util.List;


/**
 * The HandIn class represents a hand in of cards in risk game. It stores the cards which should be
 * handed in.
 *
 * @author wphung
 */

public class HandIn extends Move {

  private final List<Card> cards;

  public HandIn(List<Card> cards) {
    this.cards = cards;
  }

  /**
   * Returns the cards that are handed in.
   *
   * @return the cards that are handed in.
   */
  public List<Card> getCards() {
    return cards;
  }
}
