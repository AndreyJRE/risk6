package com.unima.risk6.game.logic.controllers;

import com.unima.risk6.game.ai.models.CountryPair;
import com.unima.risk6.game.configurations.LobbyConfiguration;
import com.unima.risk6.game.logic.Attack;
import com.unima.risk6.game.logic.EndPhase;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.logic.HandIn;
import com.unima.risk6.game.logic.Reinforce;
import com.unima.risk6.game.models.Continent;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.game.models.enums.GamePhase;
import com.unima.risk6.network.client.GameClient;
import com.unima.risk6.network.message.StandardMessage;
import java.util.ArrayList;
import java.util.List;

public class PlayerController {

  private Player player;
  private final HandController handController;

  private final GameClient gameClient;

  /**
   * Constructs a new PlayerController which has a new HandController and sets the GameClient.
   */
  public PlayerController() {
    this.handController = new HandController();
    this.gameClient = LobbyConfiguration.getGameClient();

  }

  /**
   * Sends message containing a Reinforce move that is initialized with the given parameters.
   *
   * @param reinforcedCountry the country the player wants to reinforce.
   * @param troopNumber       the amount of troops the player wants to reinforce with.
   */
  public void sendReinforce(Country reinforcedCountry, int troopNumber) {
    Reinforce reinforce = new Reinforce(reinforcedCountry, troopNumber);
    gameClient.sendMessage(new StandardMessage<>(reinforce));
  }

  /**
   * Sends a message containing an Attack move that is initialized with the given parameters.
   *
   * @param attackingCountry the country the player wants to attack with
   * @param defendingCountry the country that is under attack
   * @param troopNumber      the amount of troops the player wants to attack with
   */
  public void sendAttack(Country attackingCountry, Country defendingCountry, int troopNumber) {
    Attack attack = new Attack(attackingCountry, defendingCountry, troopNumber);
    gameClient.sendMessage(new StandardMessage<>(attack));
  }

  /**
   * Sends a message containing a Fortify move that is initialized with the given parameters.
   *
   * @param outgoing    the country the player want to fortify from
   * @param incoming    the country that is fortified
   * @param troopNumber the amount of troops the player wants to move
   */
  public void sendFortify(Country outgoing, Country incoming, int troopNumber) {
    Fortify fortify = new Fortify(outgoing, incoming, troopNumber);
    gameClient.sendMessage(new StandardMessage<>(fortify));
  }

  /**
   * Sends a message containing an EndPhase move that signals that the player wants to end the
   * current GamePhase.
   *
   * @param gamePhase the GamePhase that should be ended
   */
  public void sendEndPhase(GamePhase gamePhase) {
    EndPhase endPhase = new EndPhase(gamePhase);
    gameClient.sendMessage(new StandardMessage<>(endPhase));
  }

  /**
   * Sends a message containing a HandIn move which stores the cards that should be exchanged.
   */
  public void sendHandIn() {
    if (handController.isExchangeable()) {
      HandIn handIn = new HandIn(handController.getHand().getSelectedCards());
      //removes Cards that were selected and can be exchanged
      handController.exchangeCards();
      gameClient.sendMessage(new StandardMessage<>(handIn));
    }

  }

  /**
   * Changes the number of deployable troops of the player by the amount stated in diff.
   *
   * @param diff the number of troops that should be added or subtracted from players troops that
   *             can be deployed
   */
  public void changeDeployableTroops(int diff) {
    player.setDeployableTroops(player.getDeployableTroops() + diff);
  }

  public List<CountryPair> getValidFortifiesFromCountry(Country country) {
    List<CountryPair> fortifiable = new ArrayList<>();
    if (country.getTroops() > 1) {
      country.getAdjacentCountries().forEach((adj) -> {
        if (this.player.equals(adj.getPlayer())) {
          fortifiable.add(new CountryPair(country, adj));
        }
      });
    }
    return fortifiable;
  }

  public List<CountryPair> getValidCountryPairsFromCountry(Country country) {
    List<CountryPair> attackable = new ArrayList<>();
    if (country.getTroops() >= troopLimitPerPhase()) {
      for (Country adjacentCountry : country.getAdjacentCountries()) {
        if (!this.player.equals(adjacentCountry.getPlayer()) && adjacentCountry.hasPlayer()
            && adjacentCountry.getTroops() >= 1) {
          attackable.add(new CountryPair(country, adjacentCountry));
        }
      }
    }
    return attackable;
  }

  private int troopLimitPerPhase() {
    return this.player.getCurrentPhase() == GamePhase.REINFORCEMENT_PHASE
        || this.player.getCurrentPhase() == GamePhase.CLAIM_PHASE ? 1 : 2;
  }


  public List<CountryPair> getAllValidFortifies() {
    List<CountryPair> countriesFortifiable = new ArrayList<>();
    for (Country country : this.player.getCountries()) {
      List<CountryPair> fortifiable = getValidFortifiesFromCountry(country);
      if (fortifiable.size() > 0) {
        countriesFortifiable.addAll(fortifiable);
      }
    }
    return countriesFortifiable;
  }

  public List<CountryPair> getAllValidCountryPairs(Continent continent) {
    List<CountryPair> countriesAttackable = new ArrayList<>();
    for (Country country : continent.getCountries()) {
      List<CountryPair> attackable = new ArrayList<>();
      if (this.player.equals(country.getPlayer())) {
        attackable = getValidCountryPairsFromCountry(country);
      }
      if (attackable.size() > 0) {
        countriesAttackable.addAll(attackable);
      }
    }
    return countriesAttackable;
  }

  /**
   * Removes the country given from the set of owned countries.
   *
   * @param countryToRemove the country that should be removed from the players set of owned
   *                        countries
   */
  public void removeCountry(Country countryToRemove) {
    player.getCountries().remove(countryToRemove);
  }

  /**
   * Adds the country given from the set of owned countries. And sets the player of the countries
   *
   * @param countryToAdd the country that should be added to the set of owned countries
   */
  public void addCountry(Country countryToAdd) {
    player.getCountries().add(countryToAdd);
    countryToAdd.setPlayer(player);
  }

  /**
   * Changes the player object to be managed by the player controller.
   *
   * @param player the player that the PlayerController should now manage.
   */
  public void setPlayer(Player player) {
    this.player = player;
    this.handController.setHand(player.getHand());

  }

  public Player getPlayer() {
    return player;
  }

  public HandController getHandController() {
    return handController;
  }


  public int getNumberOfCountries() {
    return player.getCountries().size();
  }

}
