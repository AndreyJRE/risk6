package com.unima.risk6.game.logic;

import com.unima.risk6.game.models.Continent;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.game.models.enums.ContinentName;
import com.unima.risk6.game.models.enums.CountryName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Stream;

public class GameState {

  private Player[] allPlayer;
  private HashMap<CountryName, Country> countries;
  private HashMap<ContinentName, Continent> continents;
  private Player currentPlayer;


  public void initCountries() {
    Stream.of(CountryName.values())
        .forEach((n) -> countries.put(n, new Country(n)));

  }

  public void initContinents() {

  }
}
