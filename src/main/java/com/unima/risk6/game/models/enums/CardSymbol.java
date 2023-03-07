package com.unima.risk6.game.models.enums;

import com.unima.risk6.game.models.Country;
import java.util.ArrayList;
import java.util.List;

public enum CardSymbol {

  INFANTRY(List.of(CountryName.ALASKA, CountryName.ALBERTA, CountryName.WESTERN_UNITED_STATES,
      CountryName.ARGENTINA,
      CountryName.ICELAND, CountryName.WESTERN_EUROPE, CountryName.NORTH_AFRICA, CountryName.EGYPT,
      CountryName.MADAGASCAR,
      CountryName.AFGHANISTAN, CountryName.INDIA, CountryName.IRKUTSK, CountryName.JAPAN,
      CountryName.EASTERN_AUSTRALIA)),
  CANNON(List.of(CountryName.NORTH_WEST_TERRITORY, CountryName.QUEBEC,
      CountryName.EASTERN_UNITED_STATES, CountryName.VENEZUELA, CountryName.BRAZIL,
      CountryName.SCANDINAVIA, CountryName.UKRAINE,
      CountryName.EAST_AFRICA, CountryName.SOUTH_AFRICA, CountryName.SIBERIA,
      CountryName.MIDDLE_EAST, CountryName.SIAM, CountryName.MONGOLIA,
      CountryName.WESTERN_AUSTRALIA)),
  CAVALRY(List.of(CountryName.CENTRAL_AMERICA, CountryName.GREENLAND, CountryName.ONTARIO,
      CountryName.PERU, CountryName.GREAT_BRITAIN, CountryName.NORTHERN_EUROPE,
      CountryName.SOUTHERN_EUROPE, CountryName.CONGO, CountryName.URAL, CountryName.CHINA,
      CountryName.YAKUTSK, CountryName.KAMCHATKA, CountryName.INDONESIA, CountryName.NEW_GUINEA)),
  WILDCARD;
  private List<CountryName> countries;

  private CardSymbol(List<CountryName> cNames) {
    countries = cNames;

  }

  private CardSymbol() {

  }

  public List<CountryName> getCountries() {
    return countries;
  }
}
