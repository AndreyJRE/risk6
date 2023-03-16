package com.unima.risk6.game.models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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

  public void selectCard(int i) {
    if (selectedCards.size() < 3 && !selectedCards.contains(cards.get(i))) {
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
        default:
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
    if (cards.size() > 4) {
      return true;
    }
    return false;
  }

  public Set<Country> hasCountryBonus(Set<Country> countries) {
    HashSet<Country> bonusCountries = new HashSet<Country>();
    countries.forEach((country) -> {
          selectedCards.forEach((card) -> {
                if (card.getCountry().equals(country.getCountryName())) {
                  countries.add(country);
                }
              }
          );
        }
    );
    return bonusCountries;
  }

  public void exchangeCards() {
    if (isExchangable()) {
      selectedCards.forEach((n) -> cards.remove(n));
    }
  }


}
