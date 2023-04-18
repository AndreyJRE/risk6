package com.unima.risk6.game.logic.controllers;

import com.unima.risk6.game.models.Card;
import com.unima.risk6.game.models.Deck;
import com.unima.risk6.game.models.enums.CardSymbol;
import com.unima.risk6.game.models.enums.CountryName;
import java.util.Collections;
import java.util.List;

public class DeckController {

  private final Deck deck;

  public DeckController(Deck deck) {
    this.deck = deck;
  }

  public void initDeck() {
    List<CountryName> cannonCountries = List.of(CountryName.NORTH_WEST_TERRITORY,
        CountryName.QUEBEC, CountryName.EASTERN_UNITED_STATES, CountryName.VENEZUELA,
        CountryName.BRAZIL, CountryName.SCANDINAVIA, CountryName.UKRAINE, CountryName.EAST_AFRICA,
        CountryName.SOUTH_AFRICA, CountryName.SIBERIA, CountryName.MIDDLE_EAST, CountryName.SIAM,
        CountryName.MONGOLIA, CountryName.WESTERN_AUSTRALIA);
    List<CountryName> infantryCountries = List.of(CountryName.ALASKA, CountryName.ALBERTA,
        CountryName.WESTERN_UNITED_STATES, CountryName.ARGENTINA, CountryName.ICELAND,
        CountryName.WESTERN_EUROPE, CountryName.NORTH_AFRICA, CountryName.EGYPT,
        CountryName.MADAGASCAR, CountryName.AFGHANISTAN, CountryName.INDIA, CountryName.IRKUTSK,
        CountryName.JAPAN, CountryName.EASTERN_AUSTRALIA);
    List<CountryName> cavalryCountries = List.of(CountryName.CENTRAL_AMERICA, CountryName.GREENLAND,
        CountryName.ONTARIO, CountryName.PERU, CountryName.GREAT_BRITAIN,
        CountryName.NORTHERN_EUROPE, CountryName.SOUTHERN_EUROPE, CountryName.CONGO,
        CountryName.URAL, CountryName.CHINA, CountryName.YAKUTSK, CountryName.KAMCHATKA,
        CountryName.INDONESIA, CountryName.NEW_GUINEA);
    List<Card> deckCards = deck.getDeckCards();
    cannonCountries.forEach((country) -> deckCards.add(new Card(CardSymbol.CANNON, country)));
    infantryCountries.forEach((country) -> deckCards.add(new Card(CardSymbol.INFANTRY, country)));
    cavalryCountries.forEach((country) -> deckCards.add(new Card(CardSymbol.CAVALRY, country)));
    deckCards.add(new Card(CardSymbol.WILDCARD));
    deckCards.add(new Card(CardSymbol.WILDCARD));
    shuffleDeck();

  }

  public void shuffleDeck() {
    Collections.shuffle(deck.getDeckCards());

  }

  public boolean isEmpty() {
    return deck.getDeckCards().isEmpty();
  }

}
