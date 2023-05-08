package com.unima.risk6.game.logic.controllers;

import com.unima.risk6.game.models.Card;
import com.unima.risk6.game.models.Deck;
import com.unima.risk6.game.models.enums.CardSymbol;
import com.unima.risk6.game.models.enums.CountryName;
import java.util.Collections;
import java.util.List;

/**
 * This class represents the deck controller for the Risk game.
 *
 * @author wphung
 */
public class DeckController {

  private final Deck deck;
  private final Deck handedIn;
  private int nextId = 1;

  /**
   * Constructs a new DeckController with the given deck.
   *
   * @param deck The deck to be managed by this controller.
   */
  public DeckController(Deck deck) {
    this.deck = deck;
    this.handedIn = new Deck();
  }

  /**
   * Constructs a new DeckController with the given decks.
   *
   * @param deck     The deck to be managed by this controller.
   * @param handedIn the cards that were already handedIn
   */
  public DeckController(Deck deck, Deck handedIn) {
    this.deck = deck;
    this.handedIn = handedIn;
  }

  /**
   * Initializes the deck by creating and adding cards based on country names and symbols.
   */
  public void initDeck() {
    List<CountryName> cannonCountries = List.of(CountryName.NORTH_WEST_TERRITORY,
        CountryName.QUEBEC, CountryName.EASTERN_UNITED_STATES, CountryName.VENEZUELA,
        CountryName.BRAZIL, CountryName.SCANDINAVIA, CountryName.UKRAINE,
        CountryName.EAST_AFRICA,
        CountryName.SOUTH_AFRICA, CountryName.SIBERIA, CountryName.MIDDLE_EAST,
        CountryName.SIAM,
        CountryName.MONGOLIA, CountryName.WESTERN_AUSTRALIA);
    List<CountryName> infantryCountries = List.of(CountryName.ALASKA, CountryName.ALBERTA,
        CountryName.WESTERN_UNITED_STATES, CountryName.ARGENTINA, CountryName.ICELAND,
        CountryName.WESTERN_EUROPE, CountryName.NORTH_AFRICA, CountryName.EGYPT,
        CountryName.MADAGASCAR, CountryName.AFGHANISTAN, CountryName.INDIA, CountryName.IRKUTSK,
        CountryName.JAPAN, CountryName.EASTERN_AUSTRALIA);
    List<CountryName> cavalryCountries = List.of(CountryName.CENTRAL_AMERICA,
        CountryName.GREENLAND,
        CountryName.ONTARIO, CountryName.PERU, CountryName.GREAT_BRITAIN,
        CountryName.NORTHERN_EUROPE, CountryName.SOUTHERN_EUROPE, CountryName.CONGO,
        CountryName.URAL, CountryName.CHINA, CountryName.YAKUTSK, CountryName.KAMCHATKA,
        CountryName.INDONESIA, CountryName.NEW_GUINEA);
    List<Card> deckCards = deck.getDeckCards();

    for (CountryName c : cannonCountries) {
      deckCards.add(new Card(CardSymbol.CANNON, c, nextId));
      nextId++;
    }
    for (CountryName c : infantryCountries) {
      deckCards.add(new Card(CardSymbol.INFANTRY, c, nextId));
      nextId++;
    }

    for (CountryName c : cavalryCountries) {
      deckCards.add(new Card(CardSymbol.CAVALRY, c, nextId));
      nextId++;
    }
    deckCards.add(new Card(CardSymbol.WILDCARD, nextId));
    nextId++;
    deckCards.add(new Card(CardSymbol.WILDCARD, nextId));
    shuffleDeck();

  }

  /**
   * Shuffles the deck cards.
   */

  public void shuffleDeck() {
    Collections.shuffle(deck.getDeckCards());
  }

  /**
   * Checks if the deck is empty.
   *
   * @return true if the deck is empty, false otherwise.
   */
  public boolean isEmpty() {
    return deck.getDeckCards().isEmpty();
  }

  /**
   * Returns the deck managed by this controller.
   *
   * @return The deck.
   */
  public Deck getDeck() {
    return deck;
  }

  /**
   * Removes and returns the card on top of the deck.
   *
   * @return The removed card.
   */
  public Card removeCardOnTop() {
    return deck.getDeckCards().remove(0);
  }

  public Deck getHandedIn() {
    return handedIn;
  }

  public void addHandIn(List<Card> handInCards) {
    this.handedIn.getDeckCards().addAll(handInCards);
  }

  public void refillDeck() {
    deck.getDeckCards().addAll(handedIn.getDeckCards());
    handedIn.getDeckCards().clear();
    shuffleDeck();
  }
}
