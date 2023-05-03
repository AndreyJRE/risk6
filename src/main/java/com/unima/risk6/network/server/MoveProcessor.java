package com.unima.risk6.network.server;

import static com.unima.risk6.game.models.enums.GamePhase.CLAIM_PHASE;

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
import com.unima.risk6.game.models.enums.GamePhase;
import java.util.Set;

//TODO @FUNG zurückklauen
public class MoveProcessor {

  private GameController gameController;
  private PlayerController playerController;
  private DeckController deckController;

  public MoveProcessor(PlayerController playerController, GameController gameController,
      DeckController deckController) {

    this.gameController = gameController;
    this.deckController = deckController;
    this.playerController = playerController;

  }


  public void processFortify(Fortify fortify) {
    //TODO Unterscheiden ob in Fortify Phase
    gameController.addLastMove(fortify);
    //Validate if the fortify is legal
    if (fortify.getTroopsToMove() < fortify.getOutgoing().getTroops() && fortify.getOutgoing()
        .getPlayer().equals(fortify.getIncoming().getPlayer())) {
      fortify.getIncoming().changeTroops(fortify.getTroopsToMove());
      fortify.getOutgoing().changeTroops(-fortify.getTroopsToMove());
    }
    Player currentPlayer = gameController.getCurrentPlayer();
    if (currentPlayer.isHasConquered()) {
      this.drawCard();
      currentPlayer.setHasConquered(false);
    }
    if (currentPlayer.getCurrentPhase().equals(GamePhase.FORTIFY_PHASE)) {

    }


  }

  public void processReinforce(Reinforce reinforce) {
    //TODO Schick gameState an Client
    gameController.addLastMove(reinforce);
    Player currentPlayer = gameController.getCurrentPlayer();
    //Should process a Reinforce during the ClaimPhase differently
    Country countryToReinforce = reinforce.getCountry();
    if (currentPlayer.getCurrentPhase().equals(CLAIM_PHASE) && !countryToReinforce.hasPlayer()) {
      if (!countryToReinforce.hasPlayer()) {
        countryToReinforce.setPlayer(currentPlayer);
        playerController.addCountry(countryToReinforce);
      }
      //TODO Schick gameState an Client
      this.processEndPhase(new EndPhase(CLAIM_PHASE));
      currentPlayer.setInitialTroops(currentPlayer.getInitialTroops() - 1);
      countryToReinforce.setTroops(countryToReinforce.getTroops() + 1);

    } else {
      countryToReinforce.setTroops(countryToReinforce.getTroops() + reinforce.getToAdd());
      currentPlayer.setDeployableTroops(currentPlayer.getDeployableTroops() - reinforce.getToAdd());

      if (currentPlayer.getDeployableTroops() == 0) {
        //TODO Schick gameState an Client
        processEndPhase(new EndPhase(GamePhase.REINFORCEMENT_PHASE));

      }
    }
  }


  public void processAttack(Attack attack) {

    Country attackingCountry = attack.getAttackingCountry();
    Country defendingCountry = attack.getDefendingCountry();
    Player attacker = attackingCountry.getPlayer();
    Player defender = defendingCountry.getPlayer();

    attack.calculateLosses();
    gameController.addLastMove(attack);
    //TODO Schick gameState an Client
    attackingCountry.changeTroops(-attack.getAttackerLosses());
    defendingCountry.changeTroops(-attack.getDefenderLosses());

    //Increase statistics
    Statistic attackerStatistic = attacker.getStatistic();
    Statistic defenderStatistic = defender.getStatistic();
    //Increase statistics for troopsLost
    attackerStatistic.setTroopsLost(attackerStatistic.getTroopsLost() + attack.getAttackerLosses());
    defenderStatistic.setTroopsLost(defenderStatistic.getTroopsLost() + attack.getDefenderLosses());

    if (defendingCountry.getTroops() == 0) {
      gameController.getCurrentPlayer().setHasConquered(true);
      defendingCountry.setPlayer(attacker);

      playerController.addCountry(defendingCountry);
      playerController.setPlayer(defender);
      playerController.removeCountry(defendingCountry);
      playerController.setPlayer(attacker);

      //Increase statistic for countriesLost and countriesWon
      defenderStatistic.setCountriesLost(defenderStatistic.getCountriesLost() + 1);
      defenderStatistic.setCountriesWon(attackerStatistic.getCountriesWon() + 1);

      //Forced Fortify after attack and takeover
      Fortify forcedFortify = new Fortify(attackingCountry, defendingCountry,
          attack.getTroopNumber());
      processFortify(forcedFortify);
      //TODO OPTIONAL Fortify (NOW Attack has won

    }
    playerController.setPlayer(defender);
    if (playerController.getNumberOfCountries() == 0) {
      gameController.removeLostPlayer(defender);
    }
    playerController.setPlayer(attacker);

  }

  public void drawCard() {
    Card drawnCard = deckController.removeCardOnTop();
    playerController.getHandController().addCard(drawnCard);
    if (deckController.isEmpty()) {
      deckController.initDeck();
    }
  }

  public void processHandIn(HandIn handIn) {
    int numberOfHandIn = gameController.getGameState().getNumberOfHandIns();
    HandController handController = playerController.getHandController();
    if (handController.isExchangeable(handIn.getCards())) {
      Set<Country> countries = playerController.getPlayer().getCountries();
      if (!handController.hasCountryBonus(countries).isEmpty()) {
        //TODO add troops in those countries
        handController.hasCountryBonus(countries).forEach(n -> {
          //TODO SEND process/ GameState
          this.processReinforce(new Reinforce(n, 2));
        });

      }
      handController.exchangeCards();
      int diff = 0;
      if (numberOfHandIn > 5) {
        diff = 15 + 5 * (numberOfHandIn - 6);
      } else {
        diff = 2 + 2 * (numberOfHandIn);
      }
      playerController.changeDeployableTroops(diff);
      //Increase troopsGained statistic according to troops gotten through card Exchange
      Statistic statisticOfCurrentPlayer = playerController.getPlayer().getStatistic();
      statisticOfCurrentPlayer.setTroopsGained(
          statisticOfCurrentPlayer.getTroopsGained() + diff);

    }

  }

  public void processEndPhase(EndPhase endPhase) {
    gameController.addLastMove(endPhase);
    gameController.nextPhase();
  }

  public GameController getGameController() {
    return gameController;
  }
}
