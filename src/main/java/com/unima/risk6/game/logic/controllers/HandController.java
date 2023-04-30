package com.unima.risk6.game.logic.controllers;

import com.unima.risk6.game.models.Card;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.Hand;
import com.unima.risk6.game.models.enums.CardSymbol;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HandController {

  private Hand hand;
  private List<Card> selectedCards;
  private List<Card> cards;

  private int numberOfWildcards;
  private int numberOfCavalryCards;
  private int numberOfCannonCards;
  private int numberOfInfantryCards;

  //TODO Find solution for where to set Hand, current selected Cards not final for testing.
  public HandController() {

  }


  public void selectCard(int i) {
    if (selectedCards.size() < 3 && !selectedCards.contains(cards.get(i))) {
      selectedCards.add(cards.get(i));
    }
  }

  public void deselectCards(int i) {
    selectedCards.remove(cards.get(i));
  }

  public void deselectAllCards() {
    selectedCards.clear();
  }

  public boolean mustExchange() {
    return cards.size() > 4;
  }

  public void exchangeCards() {
    if (isExchangeable()) {
      selectedCards.forEach(cards::remove);
      deselectAllCards();
    }

  }

  public Set<Country> hasCountryBonus(Set<Country> countries) {
    HashSet<Country> bonusCountries = new HashSet<>();
    countries.forEach(country -> selectedCards.forEach(card -> {
              if (card.isHasCountry()) {
                if (card.getCountry().equals(country.getCountryName())) {
                  bonusCountries.add(country);
                }
              }
            }
        )

    );
    return bonusCountries;
  }

  public boolean holdsExchangeable() {
    calculateNumberOfEachCardType(cards);
    if (cards.size() < 3) {
      return false;
    }
    return (numberOfWildcards == 2 || numberOfCannonCards >= 3
        || numberOfInfantryCards >= 3 || numberOfCavalryCards >= 3) ||
        (numberOfWildcards == 1 && (numberOfCannonCards == 2
            || numberOfInfantryCards == 2 || numberOfCavalryCards == 2)) ||
        (numberOfCannonCards >= 1 && numberOfCavalryCards >= 1 && numberOfInfantryCards >= 1) ||
        (numberOfWildcards == 1 && ((numberOfCannonCards == 0 && numberOfCavalryCards == 1
            && numberOfInfantryCards == 1) || numberOfCannonCards == 1 && numberOfCavalryCards == 0
            && numberOfInfantryCards == 1) || numberOfCannonCards == 1 && numberOfCavalryCards == 1
            && numberOfInfantryCards == 0);
  }


  // Looks for a combination of cards that can be exchanged for troops
  public void selectExchangeableCards() {
    if (holdsExchangeable()) {
      int border = cards.size() - 2;
      A:
      for (int i = 0; i < border; i++) {
        selectCard(i);
        for (int j = i; j < border; j++) {
          selectCard(j + 1);
          for (int k = j; k < border; k++) {
            selectCard(k + 2);
            if (isExchangeable(selectedCards)) {
              break A;
            }
            deselectCards(k + 2);

          }
          deselectCards(j + 1);
        }
        deselectCards(i);
      }
    }
  }

  public boolean isExchangeable() {
    return isExchangeable(selectedCards);
  }

  public boolean isExchangeable(List<Card> cardList) {
    if (cardList.size() < 3) {
      return false;
    }
    calculateNumberOfEachCardType(cardList);
    return (numberOfWildcards == 2 || numberOfCannonCards == 3
        || numberOfInfantryCards == 3 || numberOfCavalryCards == 3) ||
        (numberOfWildcards == 1 && (numberOfCannonCards == 2
            || numberOfInfantryCards == 2 || numberOfCavalryCards == 2)) ||
        (numberOfCannonCards <= 1 && numberOfInfantryCards <= 1
            && numberOfCavalryCards <= 1 && numberOfWildcards <= 1);

  }

  public void calculateNumberOfEachCardType(List<Card> listToCount) {
    HashMap<CardSymbol, Integer> exchange = new HashMap<>();

    listToCount
        .forEach(
            c -> exchange.compute(c.getCardSymbol(), (key, val) -> (val == null) ? 1 : val + 1));

    numberOfWildcards =
        (exchange.get(CardSymbol.WILDCARD) == null) ? 0 : exchange.get(CardSymbol.WILDCARD);
    numberOfCannonCards =
        (exchange.get(CardSymbol.CANNON) == null) ? 0 : exchange.get(CardSymbol.CANNON);
    numberOfInfantryCards =
        (exchange.get(CardSymbol.INFANTRY) == null) ? 0 : exchange.get(CardSymbol.INFANTRY);
    numberOfCavalryCards =
        (exchange.get(CardSymbol.CAVALRY) == null) ? 0 : exchange.get(CardSymbol.CAVALRY);

  }

  public void setHand(Hand hand) {
    this.hand = hand;
    this.selectedCards = hand.getSelectedCards();
    this.cards = hand.getCards();
  }

  public void addCard(Card addedCard) {
    hand.getCards().add(addedCard);
  }


  public Hand getHand() {
    return hand;
  }
}
