package com.unima.risk6.game.models;

import com.unima.risk6.game.logic.Attack;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.logic.Reinforce;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Player {

  private Hand hand;
  private Set<Country> countries;
  private Set<Continent> continents;

  public Player() {
    this.hand = new Hand();
  }

  public Map<Country, List<Country>> getAllAttackableCountryPairs() {
    Map<Country, List<Country>> countriesAttackable = new HashMap<>();
    for (Country country : countries) {
      List<Country> attackable = getValidAttackFromCountry(country);
      countriesAttackable.put(country, attackable);
    }
    return countriesAttackable;
  }

  public List<Country> getValidAttackFromCountry(Country country) {
    // TODO
    List<Country> attackable = new ArrayList<>();
    if (country.getTroops() >= 2) {
      for (Country adjacentCountry : country.getAdjacentCountries()) {
        if (!this.equals(adjacentCountry.getPlayer())) {
          attackable.add(country);
//          switch(country.getTroops()) {
//            case 4:
//              attackable.add(new Attack(country, adjacentCountry,3 ));
//            case 3:
//              attackable.add(new Attack(country, adjacentCountry,2 ));
//            case 2:
//              attackable.add(new Attack(country, adjacentCountry,1 ));
//              break;
//            default:
//              break;
//          }
          // should go in Attack class!
        }
      }
    }
    return attackable;
  }

  public Map<Country, List<Country>> getAllValidFortifies() {
    Map<Country, List<Country>> countriesFortifiable = new HashMap<>();
    for (Country country : countries) {
      List<Country> fortifiable = getValidFortifiesFromCountry(country);
      countriesFortifiable.put(country, fortifiable);
    }
    return countriesFortifiable;
  }
  public List<Country> getValidFortifiesFromCountry(Country country) {
    // TODO: checks if more than 1 troop and if there is a path to countries
    return null;
  }

  public void drawCard(Card drawnCard){
    this.hand.addCard(drawnCard);
  }
  public int numberOfCountries() {
    return countries.size();
  }

  public Set<Country> getCountries() {
    return countries;
  }

  public void sendReinforce(Reinforce reinforce) {

  }

  public void sendAttack(Attack attack) {

  }

  public void sendFortify(Fortify fortify) {

  }

  public Hand getHand() {
    return hand;
  }
}
