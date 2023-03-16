package com.unima.risk6.game.models;

import com.unima.risk6.game.logic.Attack;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.logic.Reinforce;
import com.unima.risk6.game.models.enums.GamePhase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Player {

  private Hand hand;
  private Set<Country> countries;
  private Set<Continent> continents;
  private String user;
  private GamePhase currentPhase;
  private int deployableTroops;
  private int initialTroops;

  public Player() {
    this.hand = new Hand();
    countries = new HashSet<>();
    continents = new HashSet<>();
  }

  public Player(String user) {
    this.hand = new Hand();
    countries = new HashSet<>();
    continents = new HashSet<>();
    this.user = user;
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
    List<Country> fortifiable = new ArrayList<Country>();
    country.getAdjacentCountries().forEach((n) -> {
      if (n.getPlayer().equals(this)) {
        fortifiable.add(n);
      }
    });
    return null;
  }

  /**
   * @param drawnCard
   */
  public void drawCard(Card drawnCard) {
    this.hand.addCard(drawnCard);
  }

  /**
   * @param numberOfHandIn
   */
  public void handInCards(int numberOfHandIn) {
    if (hand.isExchangable()) {
      if (!hand.hasCountryBonus(countries).isEmpty()) {
        //TODO send REINFORCE or add troops
        hand.hasCountryBonus(countries).forEach(n -> sendReinforce(n, 2));
      }
      hand.exchangeCards();
      if (numberOfHandIn > 5) {
        deployableTroops += 15 + 5 * (numberOfHandIn - 6);
      } else {
        deployableTroops += 2 + 2 * (numberOfHandIn);
      }

    }

  }

  public void selectCard(int i) {
    hand.selectCard(i);
  }

  public void deselectCard(int i) {
    hand.deselectCards(i);
  }

  public int numberOfCountries() {
    return countries.size();
  }

  public Set<Country> getCountries() {
    return countries;
  }

  public void addCountry(Country countryToAdd) {
    countries.add(countryToAdd);
    countryToAdd.setPlayer(this);
  }

  public void removeCountry(Country countryToRemove) {
    countries.remove(countryToRemove);
  }

  //TODO expand with CLAIMPHASE
  public GamePhase nextPhase() {
    switch (currentPhase) {
      case REINFORCEMENTPHASE:
        if (deployableTroops == 0) {
          this.setPhase(GamePhase.ATTACKPHASE);
          return GamePhase.ATTACKPHASE;
        } else {
          return GamePhase.REINFORCEMENTPHASE;
          //TODO exception or error which should be given to UI
        }

      case ATTACKPHASE:
        this.setPhase(GamePhase.FORTIFYPHASE);
        return GamePhase.FORTIFYPHASE;
      case FORTIFYPHASE, CLAIMPHASE:
        this.setPhase(GamePhase.NOTACTIVE);
        return GamePhase.NOTACTIVE;
      case NOTACTIVE:
        this.setPhase(GamePhase.REINFORCEMENTPHASE);
        return GamePhase.REINFORCEMENTPHASE;

      default:
        return GamePhase.REINFORCEMENTPHASE;


    }

  }

  public int getDeployableTroops() {
    return deployableTroops;
  }

  public void calculateDeployableTroops() {
    deployableTroops = 3;
    int n = this.getNumberOfCountries();
    if (n > 8) {
      n = n - 9;
      deployableTroops += Math.floorDiv(n, 3);
    }
    continents.forEach((x) -> deployableTroops += x.getBonusTroops());
  }

  //TODO have to implement which Continent is fully Occupied by Player
  public void updateContinents(Set<Continent> continents) {
    continents.forEach((n) -> {
      if (countries.containsAll(n.getCountries())) {
        this.continents.add(n);
      }
    });

  }

  public Reinforce sendReinforce(Country reinforcedCountry, int troopNumber) {
    return new Reinforce(reinforcedCountry, troopNumber);
  }

  public Attack sendAttack(Country attackingCountry, Country defendingCountry, int troopNumber) {

    return new Attack(attackingCountry, defendingCountry, troopNumber);
  }

  public Fortify sendFortify(Country outgoing, Country incoming, int troopNumber) {
    return new Fortify(outgoing, incoming, troopNumber);
  }

  public Hand getHand() {
    return hand;
  }

  public int getNumberOfCountries() {
    return countries.size();
  }

  public GamePhase getPhase() {
    return this.currentPhase;
  }

  public void setPhase(GamePhase phase) {
    this.currentPhase = phase;
  }

  public GamePhase getCurrentPhase() {
    return currentPhase;
  }

  public Set<Continent> getContinents() {
    return continents;
  }

  public void setInitialTroops(int initialTroops) {
    this.initialTroops = initialTroops;
  }

  public int getInitialTroops() {
    return initialTroops;
  }
}

