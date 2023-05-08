package com.unima.risk6.game.logic.controllers;

import com.unima.risk6.game.models.Card;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.Hand;
import com.unima.risk6.game.models.enums.CardSymbol;
import com.unima.risk6.game.models.enums.CountryName;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class represents the hand controller for the Risk game.
 *
 * @author wphung
 */

public class HandController {

  private Hand hand;
  private List<Card> selectedCards;
  private List<Card> cards;

  private int numberOfWildcards;
  private int numberOfCavalryCards;
  private int numberOfCannonCards;
  private int numberOfInfantryCards;

  //TODO Find solution for where to set Hand, current selected Cards not final for testing.

  /**
   * Constructs a new HandController.
   */
  public HandController() {

  }

  /**
   * Selects a card from the hand by its index.
   *
   * @param i The index of the card in the hand.
   */
  public void selectCardThroughIndex(int i) {
    if (selectedCards.size() < 3 && !selectedCards.contains(cards.get(i))) {
      selectedCards.add(cards.get(i));
    }
  }


  /**
   * Selects a card from the hand by the card object.
   *
   * @param c The card object to be selected.
   */
  public void selectCardThroughCard(Card c) {
    if (selectedCards.size() < 3 && !selectedCards.contains(c)) {
      selectedCards.add(c);
    }
  }

  /**
   * Selects a list of cards through calling the selectCardThroughId method
   *
   * @param cardsToSelect The list of card objects to be selected.
   */
  public void selectCardsFromCardList(List<Card> cardsToSelect) {
    cards.stream().filter(cardsToSelect::contains).forEach(this::selectCardThroughCard);
  }

  /**
   * Deselects a card from the hand by its index.
   *
   * @param i The index of the card in the hand.
   */
  public void deselectCardsThroughIndex(int i) {
    selectedCards.remove(cards.get(i));
  }

  /**
   * Deselects all cards from the hand.
   */
  public void deselectAllCards() {
    selectedCards.clear();
  }

  /**
   * Determines if the player must exchange cards for troops based on the number of cards held.
   *
   * @return true if the player must exchange cards, false otherwise.
   */
  public boolean mustExchange() {
    return cards.size() > 4;
  }

  /**
   * Deletes the cards that are exchanged in the selected cards List and the cards list
   */
  public void exchangeCards() {
    selectedCards.stream().filter(cards::contains).forEach(cards::remove);
    deselectAllCards();
  }

  /**
   * Returns the set of bonus countries based on the selected cards.
   *
   * @param countries The set of countries to consider for bonus.
   * @return The set of bonus countries.
   */
  public Set<CountryName> getBonusCountries(Set<Country> countries) {
    HashSet<CountryName> bonusCountries = new HashSet<>();
    countries.forEach(country -> selectedCards.forEach(card -> {
              if (card.hasCountry()) {
                if (card.getCountry().equals(country.getCountryName())) {
                  bonusCountries.add(country.getCountryName());
                }
              }
            }
        )

    );
    return bonusCountries;
  }

  /**
   * Determines if the hand holds exchangeable cards.
   *
   * @return true if the hand holds exchangeable cards, false otherwise.
   */
  public boolean holdsExchangeable() {
    calculateNumberOfEachCardType(cards);
    if (cards.size() < 3) {
      return false;
    }
    return (numberOfWildcards == 2 || numberOfCannonCards >= 3
        || numberOfInfantryCards >= 3 || numberOfCavalryCards >= 3)
        || (numberOfWildcards == 1 && (numberOfCannonCards == 2
        || numberOfInfantryCards == 2 || numberOfCavalryCards == 2))
        || (numberOfCannonCards >= 1 && numberOfCavalryCards >= 1 && numberOfInfantryCards >= 1)
        || (numberOfWildcards == 1 && ((numberOfCannonCards == 0 && numberOfCavalryCards == 1
        && numberOfInfantryCards == 1) || numberOfCannonCards == 1 && numberOfCavalryCards == 0
        && numberOfInfantryCards == 1) || numberOfCannonCards == 1 && numberOfCavalryCards == 1
        && numberOfInfantryCards == 0);
  }


  /**
   * Selects a combination of exchangeable cards from the hand.
   */
  public void selectExchangeableCards() {
    deselectAllCards();
    int border = cards.size() - 2;
    A:
    for (int i = 0; i < border; i++) {
      selectCardThroughIndex(i);
      for (int j = i; j < border; j++) {
        selectCardThroughIndex(j + 1);
        for (int k = j; k < border; k++) {
          selectCardThroughIndex(k + 2);
          if (isExchangeable(selectedCards)) {
            break A;
          }
          deselectCardsThroughIndex(k + 2);

        }
        deselectCardsThroughIndex(j + 1);
      }
      deselectCardsThroughIndex(i);
    }
  }

  /**
   * Determines if the selected cards are exchangeable.
   *
   * @return true if the selected cards are exchangeable, false otherwise.
   */
  public boolean isExchangeable() {
    return isExchangeable(selectedCards);
  }

  /**
   * Determines if the given list of cards is exchangeable.
   *
   * @param cardList The list of cards to evaluate.
   * @return true if the list of cards is exchangeable, false otherwise.
   */
  public boolean isExchangeable(List<Card> cardList) {
    if (cardList.size() < 3) {
      return false;
    }
    calculateNumberOfEachCardType(cardList);
    return (numberOfWildcards == 2 || numberOfCannonCards == 3
        || numberOfInfantryCards == 3 || numberOfCavalryCards == 3)
        || (numberOfWildcards == 1 && (numberOfCannonCards == 2
        || numberOfInfantryCards == 2 || numberOfCavalryCards == 2))
        || (numberOfCannonCards <= 1 && numberOfInfantryCards <= 1
        && numberOfCavalryCards <= 1 && numberOfWildcards <= 1);

  }

  /**
   * Counts the number of cards of each card type of the given List and saves it in the global
   * variables
   *
   * @param listToCount list of cards to count the number of cards for each card type from.
   */
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

  /**
   * Adds a card to the hand.
   *
   * @param addedCard The card to be added.
   */
  public void addCard(Card addedCard) {
    hand.getCards().add(addedCard);
  }

  /**
   * Returns the hand managed by this controller.
   *
   * @return the Hand.
   */
  public Hand getHand() {
    return hand;
  }

  /**
   * Changes the Hand object to be managed by the player controller
   *
   * @param hand the Hand that the HandController should now manage.
   */
  public void setHand(Hand hand) {
    this.hand = hand;
    this.selectedCards = hand.getSelectedCards();
    this.cards = hand.getCards();
  }
}
