package com.unima.risk6.game.logic;

import com.unima.risk6.game.models.Card;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.Hand;
import com.unima.risk6.game.models.enums.CardSymbol;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class HandController {

  private Hand hand;
  private ArrayList<Card> cards;
  private ArrayList<Card> selectedCards;
  private DeckController deckController;

  public HandController(Hand hand) {
    this.hand = hand;
    cards = hand.getCards();
    selectedCards = hand.getSelectedCards();

  }


  public void drawCard() {
    cards.add(deckController.drawCard());
  }

  public void selectCard(int i) {
    if (selectedCards.size() < 3 && !selectedCards.contains(cards.get(i))) {
      selectedCards.add(cards.get(i));
    }
  }

  public void deselectCards(int i) {
    selectedCards.remove(cards.get(i));
  }

  //TODO Mach Hashmap


  public boolean mustExchange() {
    if (cards.size() > 4) {
      return true;
    }
    return false;
  }

  public void exchangeCards() {
    if (isExchangable()) {
      selectedCards.forEach((n) -> cards.remove(n));
    }
  }

  public void receiveCards(ArrayList<Card> receivedCards) {
    receivedCards.forEach(n -> hand.getCards().add(n));
  }

  public Set<Country> hasCountryBonus(Set<Country> countries) {
    HashSet<Country> bonusCountries = new HashSet<Country>();
    countries.forEach((country) -> selectedCards.forEach((card) -> {
              if (card.getCountry() != null) {
                if (card.getCountry().equals(country.getCountryName())) {
                  countries.add(country);
                }
              }
            }
        )

    );
    return bonusCountries;
  }

  public boolean isExchangable() {
    HashMap<CardSymbol, Integer> exchange = new HashMap<>();

    selectedCards
        .forEach(
            c -> exchange.compute(c.getCardSymbol(), (key, val) -> (val == null) ? 1 : val + 1));

    Integer numberOfWildcards = exchange.get(CardSymbol.WILDCARD);
    Integer numberOfCannonCards = exchange.get(CardSymbol.CANNON);

    Integer numberOfInfantryCards = exchange.get(CardSymbol.INFANTRY);
    if (numberOfWildcards == 2 || numberOfCannonCards == 3
        || numberOfInfantryCards == 3 || exchange.get(CardSymbol.CAVALRY) == 3) {
      return true;
    }
    if (numberOfWildcards == 1 && (numberOfCannonCards == 2
        || numberOfInfantryCards == 2 || exchange.get(CardSymbol.CAVALRY) == 2)) {
      return true;
    }
    if (numberOfCannonCards <= 1 && numberOfInfantryCards <= 1
        && exchange.get(CardSymbol.CAVALRY) <= 1 && numberOfWildcards <= 1) {
      return true;
    }
    return false;
  }
}
