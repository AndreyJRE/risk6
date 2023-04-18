package com.unima.risk6.game.logic.controllers;

import com.unima.risk6.game.models.Card;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.Deck;
import com.unima.risk6.game.models.Hand;
import com.unima.risk6.game.models.enums.CardSymbol;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HandController {

  private Hand hand;
  private Deck deck;
  private final List<Card> selectedCards;
  private final List<Card> cards;

  public HandController() {
    selectedCards = hand.getSelectedCards();
    cards = hand.getCards();
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
    return cards.size() > 4;
  }

  public void exchangeCards() {
    if (isExchangable()) {
      selectedCards.forEach(cards::remove);
    }
  }

  public Set<Country> hasCountryBonus(Set<Country> countries) {
    HashSet<Country> bonusCountries = new HashSet<>();
    countries.forEach(country -> selectedCards.forEach(card -> {
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
    return (numberOfWildcards == 2 || numberOfCannonCards == 3
        || numberOfInfantryCards == 3 || exchange.get(CardSymbol.CAVALRY) == 3) ||
        (numberOfWildcards == 1 && (numberOfCannonCards == 2
            || numberOfInfantryCards == 2 || exchange.get(CardSymbol.CAVALRY) == 2)) ||
        (numberOfCannonCards <= 1 && numberOfInfantryCards <= 1
            && exchange.get(CardSymbol.CAVALRY) <= 1 && numberOfWildcards <= 1);
  }

  public Card drawCard() {
    return deck.getDeckCards().remove(0);

  }

  public void setDeck(Deck deck) {
    this.deck = deck;
  }

  public void setHand(Hand hand) {
    this.hand = hand;
  }

  public Deck getDeck() {
    return deck;
  }

  public Hand getHand() {
    return hand;
  }
}
