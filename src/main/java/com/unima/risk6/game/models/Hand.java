package com.unima.risk6.game.models;

import com.unima.risk6.game.models.enums.CardSymbol;
import com.unima.risk6.game.models.enums.CountryName;
import java.util.ArrayList;

public class Hand {

  private ArrayList<Card> cards;
  private ArrayList<Card> selectedCards;


  public Hand() {
    ArrayList<Card> cards = new ArrayList<Card>();
    ArrayList<Card> selectedCards = new ArrayList<Card>();
  }

  public void addCard(Card cardToAdd) {
    cards.add(cardToAdd);

  }

  public void selectCards(int i) {
    if (selectedCards.size() < 3) {
      selectedCards.add(cards.get(i));
    }
  }

  public void deselectCards(int i) {
    selectedCards.remove(cards.get(i));
  }


  public boolean isExchangable() {

    int cannonCards = 0;
    int infantryCards = 0;
    int cavalryCards = 0;
    int wildCards = 0;
    int difCardTypes = 0;
    for (Card c : selectedCards) {
      switch (c.getCardSymbol()) {
        case CANNON:
          cannonCards++;
          break;
        case CAVALRY:
          cavalryCards++;
          break;
        case INFANTRY:
          infantryCards++;
          break;
        case WILDCARD:
          wildCards++;
          break;
      }
    }

    if (wildCards == 2 || cannonCards == 3 || infantryCards == 3 || cavalryCards == 3) {
      return true;
    }
    if (wildCards == 1 && (cannonCards == 2 || infantryCards == 2 || cavalryCards == 2)) {
      return true;
    }
    if (cannonCards <= 1 && infantryCards <= 1 && cavalryCards <= 1 && wildCards <= 1) {
      return true;
    }
    return false;
  }

  public boolean mustExchange() {
    if (cards.size() == 5) {
      return true;
    }
    return false;
  }

  public void exchangeCards() {
    selectedCards.forEach((n) -> cards.remove(n));
  }


}
