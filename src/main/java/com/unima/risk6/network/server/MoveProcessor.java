package com.unima.risk6.network.server;

import static com.unima.risk6.game.models.enums.GamePhase.ATTACK_PHASE;
import static com.unima.risk6.game.models.enums.GamePhase.CLAIM_PHASE;
import static com.unima.risk6.game.models.enums.GamePhase.FORTIFY_PHASE;
import static com.unima.risk6.game.models.enums.GamePhase.REINFORCEMENT_PHASE;

import com.unima.risk6.game.logic.Attack;
import com.unima.risk6.game.logic.EndPhase;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.logic.HandIn;
import com.unima.risk6.game.logic.Reinforce;
import com.unima.risk6.game.logic.controllers.DeckController;
import com.unima.risk6.game.logic.controllers.GameController;
import com.unima.risk6.game.logic.controllers.HandController;
import com.unima.risk6.game.logic.controllers.PlayerController;
import com.unima.risk6.game.models.Card;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.game.models.Statistic;
import com.unima.risk6.game.models.enums.CountryName;
import com.unima.risk6.network.server.exceptions.InvalidMoveException;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class handles incoming Moves objects and uses  GameController, PlayerController and
 * DeckController objects to manipulate the GameState according to the incoming move.
 *
 * @author wphung
 */
public class MoveProcessor {

  private GameController gameController;
  private PlayerController playerController;
  private DeckController deckController;

  /**
   * Constructs a MoveProcessor object with the given controllers.
   *
   * @param playerController a PlayerController object that is used to manage a Player object
   * @param deckController   a DeckController object that is used to manage a Deck object
   * @param gameController   a GameController object that is used to manage a GameState object.
   */
  public MoveProcessor(PlayerController playerController, GameController gameController,
      DeckController deckController) {
    this.gameController = gameController;
    this.deckController = deckController;
    this.playerController = playerController;
  }

  public MoveProcessor() {
  }

  /**
   * Validates if the Reinforce move is legal and if it is, GameState is updated according to the
   * Reinforce move received. If the Reinforce move is not legal an InvalidMoveException is thrown.
   *
   * @param reinforce the Reinforce move that should be processed.
   */
  public void processReinforce(Reinforce reinforce) {
    Player currentPlayer = gameController.getCurrentPlayer();
    Country countryToReinforce = getCountryByCountryName(reinforce.getCountry().getCountryName());

    if ((currentPlayer.getCurrentPhase().equals(REINFORCEMENT_PHASE)
        && countryToReinforce.getPlayer().equals(currentPlayer)
        && reinforce.getToAdd() <= currentPlayer.getDeployableTroops())
        || currentPlayer.getCurrentPhase().equals(CLAIM_PHASE)) {

      //Should process a Reinforce during the ClaimPhase differently
      if (currentPlayer.getCurrentPhase().equals(CLAIM_PHASE)) {
        //If the country doesn't have an owner, owner is assigned to currentPlayer, if it does
        //check if the Country actually belongs to player

        int numberOfNeutralCountries = gameController.getGameState().getCountries().stream()
            .filter(n -> !n.hasPlayer()).collect(Collectors.toSet()).size();

        //Check if there are still neutral countries to reinforce
        if (numberOfNeutralCountries > 0) {
          if (!countryToReinforce.hasPlayer()) {
            playerController.addCountry(countryToReinforce);
          } else {
            if (countryToReinforce.getPlayer().equals(currentPlayer)) {
              throw new InvalidMoveException(
                  "There are still neutral countries that need to be claimed");
            } else {
              throw new InvalidMoveException("Cannot claim or reinforce country that is not yours");
            }
          }
        }

        if (countryToReinforce.getPlayer().equals(currentPlayer)) {
          currentPlayer.setInitialTroops(currentPlayer.getInitialTroops() - 1);
          countryToReinforce.setTroops(countryToReinforce.getTroops() + 1);
          this.updateInGameStatistics();
        } else {
          throw new InvalidMoveException("The Reinforce is not valid because country is not yours");
        }

        //After a reinforce in claim phase the turn of the player should end.
        //this.processEndPhase(new EndPhase(CLAIM_PHASE));

      } else {
        //Add armies to country to reinforce.
        countryToReinforce.setTroops(countryToReinforce.getTroops() + reinforce.getToAdd());
        currentPlayer.setDeployableTroops(
            currentPlayer.getDeployableTroops() - reinforce.getToAdd());
      }
      gameController.addLastMove(reinforce);
    } else {

      if (!currentPlayer.getCurrentPhase().equals(REINFORCEMENT_PHASE)) {
        throw new InvalidMoveException(
            "The Reinforce is not valid because currentPlayer is not in ReinforcePhase ");
      }
      if (!countryToReinforce.getPlayer().equals(currentPlayer)) {
        throw new InvalidMoveException("The Reinforce is not valid because of not your country ");
      }
      if (reinforce.getToAdd() > currentPlayer.getDeployableTroops()) {
        throw new InvalidMoveException(
            "The Reinforce is not valid because of no more deployable troops ");
      }
      if (!currentPlayer.getCurrentPhase().equals(CLAIM_PHASE)) {
        throw new InvalidMoveException("The Reinforce is not valid because of not in claim phase");
      }
      throw new InvalidMoveException("The Reinforce is not valid because of unexpected Situation");
    }
    this.updateInGameStatistics();
  }

  /**
   * Validates if the Attack move is legal and if it is, the GameState is updated according to the
   * Attack move received. If the Attack move is illegal an InvalidMoveExcepiton is thrown.
   *
   * @param attack the Attack move that should be processed.
   */
  public boolean processAttack(Attack attack) {
    Country attackingCountry = getCountryByCountryName(
        attack.getAttackingCountry().getCountryName());

    Country defendingCountry = getCountryByCountryName(
        attack.getDefendingCountry().getCountryName());

    Player attacker = getPlayerFromCurrentState(attackingCountry.getPlayer());
    Player defender = getPlayerFromCurrentState(defendingCountry.getPlayer());

    //check if attack is legal.
    Player currentPlayer = gameController.getCurrentPlayer();
    if (attacker.equals(currentPlayer) && !attacker.equals(defender)
        && currentPlayer.getCurrentPhase().equals(ATTACK_PHASE)
        && defendingCountry.getAdjacentCountries().contains(attackingCountry)
        && attack.getTroopNumber() <= attackingCountry.getTroops() - 1
        && attack.getTroopNumber() < 4 && attack.getTroopNumber() > 0) {
      attack.calculateLosses();

      gameController.addLastMove(attack);

      attackingCountry.changeTroops(-attack.getAttackerLosses());
      defendingCountry.changeTroops(-attack.getDefenderLosses());

      //Increase statistics
      Statistic attackerStatistic = attacker.getStatistic();
      Statistic defenderStatistic = defender.getStatistic();
      //Increase statistics for troopsLost
      attackerStatistic.setTroopsLost(
          attackerStatistic.getTroopsLost() + attack.getAttackerLosses());

      defenderStatistic.setTroopsLost(
          defenderStatistic.getTroopsLost() + attack.getDefenderLosses());

      //Take over a country if the attack has wiped out the troops on the defending country
      if (attack.getHasConquered()) {
        currentPlayer.setHasConquered(true);
        defendingCountry.setPlayer(attacker);

        playerController.addCountry(defendingCountry);
        playerController.setPlayer(defender);
        playerController.removeCountry(defendingCountry);
        playerController.setPlayer(attacker);

        //Increase statistic for countriesLost and countriesWon

        defenderStatistic.setCountriesLost(defenderStatistic.getCountriesLost() + 1);
        defenderStatistic.setNumberOfTroops(0);
        defenderStatistic.setNumberOfOwnedCountries(0);

        attackerStatistic.setCountriesWon(attackerStatistic.getCountriesWon() + 1);

        playerController.setPlayer(defender);
        if (playerController.getNumberOfCountries() == 0) {
          gameController.removeLostPlayer(defender);
        }

      }
      playerController.setPlayer(attacker);
    } else {
      if (!(attacker.equals(currentPlayer) && !attacker.equals(defender))) {
        throw new InvalidMoveException(
            "Invalid Attack made by not current player or attacker tries to attack himself");
      }
      if (!(currentPlayer.getCurrentPhase().equals(ATTACK_PHASE))) {
        throw new InvalidMoveException("Invalid Attack because not in attack phase");
      }
      if (!defendingCountry.getAdjacentCountries().contains(attackingCountry)) {
        throw new InvalidMoveException("Invalid attack because not adcjecent country");
      }
      if (!(attack.getTroopNumber() <= attackingCountry.getTroops() - 1)) {
        throw new InvalidMoveException(
            "invalid attack because of not valid troop size to attack with");
      }
      if (!(attack.getTroopNumber() < 4 && attack.getTroopNumber() > 0)) {
        throw new InvalidMoveException("Invalid attack because of not valid troop size ");
      }

    }
    this.updateInGameStatistics();
    return gameController.getGameState().isGameOver();
  }

  /**
   * Validates if the fortify is legal and if it is updates the game state according to the Fortify
   * move received.
   *
   * @param fortify the Fortify move that should be processed.
   */
  public void processFortify(Fortify fortify) throws InvalidMoveException {

    //Gets the reference of the country of the server side game state
    // from the CountryName of fortify.
    Country incoming = getCountryByCountryName(fortify.getIncoming().getCountryName());
    Country outgoing = getCountryByCountryName(fortify.getOutgoing().getCountryName());

    //Validate if the fortify is legal
    if (fortify.getTroopsToMove() < outgoing.getTroops()
        && outgoing.getPlayer().equals(incoming.getPlayer())
        && incoming.getAdjacentCountries().contains(outgoing)
        && fortify.getTroopsToMove() >= 0
        && outgoing.getPlayer().equals(gameController.getCurrentPlayer())
        && (gameController.getCurrentPlayer().getCurrentPhase().equals(ATTACK_PHASE)
        || gameController.getCurrentPlayer().getCurrentPhase().equals(FORTIFY_PHASE))) {

      incoming.changeTroops(fortify.getTroopsToMove());
      outgoing.changeTroops(-fortify.getTroopsToMove());
      gameController.addLastMove(fortify);

    } else {
      throw new InvalidMoveException("The Fortify is not valid");
    }

  }

  /**
   * Draws a Card from the deck and assigns it to the Hand of the current Player.
   */
  public void drawCard() {
    Card drawnCard = deckController.removeCardOnTop();
    playerController.getHandController().addCard(drawnCard);
    if (deckController.isEmpty()) {
      deckController.refillDeck();
    }
  }

  /**
   * Processes a HandIn move of cards and increases the deployable troops of the current Player
   * according to the number of hand ins that had already occurred.
   *
   * @param handIn the HandIn move that should be processed.
   */
  public void processHandIn(HandIn handIn) {
    HandController handController = playerController.getHandController();
    handController.selectCardsFromCardList(handIn.getCards());

    //Checks if HandIn is valid.
    if (gameController.getCurrentPlayer().getCurrentPhase().equals(REINFORCEMENT_PHASE)
        && handController.isExchangeable(handController.getHand().getSelectedCards())) {
      deckController.addHandIn(handIn.getCards());
      gameController.addLastMove(handIn);
      //Adds the deployable troops according to the number of hand in.
      int numberOfHandIn = gameController.getGameState().getNumberOfHandIns();
      int diff;
      if (numberOfHandIn > 5) {
        diff = 15 + 5 * (numberOfHandIn - 6);
      } else {
        diff = 2 + 2 * (numberOfHandIn);
      }
      playerController.changeDeployableTroops(diff);

      //Sends the bonus troops if countries are the same.
      Set<CountryName> bonusCountries = handController.getBonusCountries(
          playerController.getPlayer().getCountries());
      if (!bonusCountries.isEmpty()) {
        for (CountryName c : bonusCountries) {
          playerController.changeDeployableTroops(2);

          this.processReinforce(new Reinforce(getCountryByCountryName(c), 2));

        }
      }

      //Increase troopsGained statistic according to troops gotten through card Exchange
      Statistic statisticOfCurrentPlayer = playerController.getPlayer().getStatistic();

      statisticOfCurrentPlayer.setTroopsGained(
          statisticOfCurrentPlayer.getTroopsGained() + diff);

      gameController.getGameState()
          .setNumberOfHandIns(gameController.getGameState().getNumberOfHandIns() + 1);
      handController.exchangeCards();
    } else {
      handController.deselectAllCards();
      throw new InvalidMoveException("The HandIn is not valid");
    }

  }

  /**
   * Processes an EndPhase move of and changes the GamePhase into the next phase according to the
   * current GameState and GamePhase.
   *
   * @param endPhase the EndPhase move that should be processed.
   */
  public void processEndPhase(EndPhase endPhase) {
    Player currentPlayer = gameController.getCurrentPlayer();
    if (currentPlayer.getHasConquered() && playerController.getPlayer().getCurrentPhase()
        .equals(ATTACK_PHASE)) {
      this.drawCard();
      currentPlayer.setHasConquered(false);
    }

    if (currentPlayer.getDeployableTroops() == 0 || !playerController.getPlayer().getCurrentPhase()
        .equals(REINFORCEMENT_PHASE)) {
      gameController.addLastMove(new EndPhase(gameController.getCurrentPlayer().getCurrentPhase()));
      gameController.nextPhase();
      playerController.setPlayer(gameController.getCurrentPlayer());


    } else {
      //Throw invalid move exception
      throw new InvalidMoveException("Cannot End Phase yet");
    }

  }

  /**
   * Gets the Country object in GameState by the given CountryName.
   *
   * @param countryName the name of the Country as represented by the CountryName enum.
   */
  public Country getCountryByCountryName(CountryName countryName) {
    final Country[] country = new Country[1];
    gameController.getGameState().getCountries().stream()
        .filter(n -> n.getCountryName().equals(countryName)).forEach(n -> country[0] = n);
    return country[0];
  }

  /**
   * Gets the player object reference of the game state that has the same username as the player
   * object specified as parameter.
   *
   * @param player the player whose corresponding player object in the GameState should be gotten
   * @return Returns the player from the GameState.
   */
  public Player getPlayerFromCurrentState(Player player) {
    return gameController.getGameState().getActivePlayers().stream().filter(p -> p.equals(player))
        .findFirst().orElse(null);
  }

  /**
   * Updated the in game statistics of each player. these include owned countries and troops.
   */
  public void updateInGameStatistics() {
    HashMap<Player, Integer> troopCounter = gameController.countTroops();
    HashMap<Player, Integer> countryCounter = gameController.countCountries();
    gameController.getGameState().getActivePlayers()
        .forEach(n -> n.getStatistic().setNumberOfOwnedCountries(countryCounter.get(n)));
    gameController.getGameState().getActivePlayers()
        .forEach(n -> n.getStatistic().setNumberOfTroops(troopCounter.get(n)));
  }

  /**
   * Clears the LastMoves ArrayList in GameState.
   */
  public void clearLastMoves() {
    gameController.getGameState().getLastMoves().clear();
  }

  public GameController getGameController() {
    return gameController;
  }

  public PlayerController getPlayerController() {
    return playerController;
  }

  public DeckController getDeckController() {
    return deckController;
  }

  public void setGameController(GameController gameController) {
    this.gameController = gameController;
  }

  public void setPlayerController(PlayerController playerController) {
    this.playerController = playerController;
  }

  public void setDeckController(DeckController deckController) {
    this.deckController = deckController;
  }
}

