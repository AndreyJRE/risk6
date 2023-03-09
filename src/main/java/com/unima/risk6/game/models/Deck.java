package com.unima.risk6.game.models;

import com.unima.risk6.game.models.enums.CardSymbol;
import java.util.ArrayList;
import java.util.Collections;
import com.unima.risk6.game.models.enums.CountryName;
import java.util.List;

public class Deck {

  private ArrayList<Card> deckCards;


  public Deck() {
    deckCards = new ArrayList<Card>();
    initDeck();


  }

  public void initDeck() {
    List<CountryName> cannonCountries = List.of(CountryName.NORTH_WEST_TERRITORY,
        CountryName.QUEBEC,
        CountryName.EASTERN_UNITED_STATES, CountryName.VENEZUELA, CountryName.BRAZIL,
        CountryName.SCANDINAVIA, CountryName.UKRAINE,
        CountryName.EAST_AFRICA, CountryName.SOUTH_AFRICA, CountryName.SIBERIA,
        CountryName.MIDDLE_EAST, CountryName.SIAM, CountryName.MONGOLIA,
        CountryName.WESTERN_AUSTRALIA);
    List<CountryName> infantryCountries = List.of(CountryName.ALASKA, CountryName.ALBERTA,
        CountryName.WESTERN_UNITED_STATES,
        CountryName.ARGENTINA,
        CountryName.ICELAND, CountryName.WESTERN_EUROPE, CountryName.NORTH_AFRICA,
        CountryName.EGYPT,
        CountryName.MADAGASCAR,
        CountryName.AFGHANISTAN, CountryName.INDIA, CountryName.IRKUTSK, CountryName.JAPAN,
        CountryName.EASTERN_AUSTRALIA);
    List<CountryName> cavalryCountries = List.of(CountryName.CENTRAL_AMERICA, CountryName.GREENLAND,
        CountryName.ONTARIO,
        CountryName.PERU, CountryName.GREAT_BRITAIN, CountryName.NORTHERN_EUROPE,
        CountryName.SOUTHERN_EUROPE, CountryName.CONGO, CountryName.URAL, CountryName.CHINA,
        CountryName.YAKUTSK, CountryName.KAMCHATKA, CountryName.INDONESIA, CountryName.NEW_GUINEA);

    cannonCountries
        .forEach((country) -> deckCards.add(new Card(CardSymbol.CANNON, country)));
    infantryCountries
        .forEach((country) -> deckCards.add(new Card(CardSymbol.INFANTRY, country)));
    cavalryCountries
        .forEach((country) -> deckCards.add(new Card(CardSymbol.CAVALRY, country)));
    deckCards.add(new Card(CardSymbol.WILDCARD));
    deckCards.add(new Card(CardSymbol.WILDCARD));

  }

  public void shuffleDeck() {
    Collections.shuffle(deckCards);

  }

  public Card drawCard() {
    Card drawnCard = deckCards.get(0);
    deckCards.remove(0);
    return drawnCard;

  }

  public ArrayList<Card> getDeckCards() {
    return deckCards;
  }
}
