package com.unima.risk6.game.logic;

import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.game.models.enums.GamePhase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;
import java.util.Set;

public class GameController {

  private GameState gameState;

  private boolean hasConquered;
  private Queue<Player> players;
  private Set<Country> countries;
  private HashMap<Player, Integer> initialTroops;

  public GameController(GameState gameState) {
    this.gameState = gameState;
  }
  //TODO IMPLEMENT ROUNDBASED TURN CONTROL


  //TODO EXPAND with CLAIMPHASE
  public void nextPhase() {
    if (gameState.getCurrentPlayer().getPhase().equals(GamePhase.FORTIFYPHASE)) {
      this.nextPlayer();
    } else {
      gameState.setCurrentPhase(gameState.getCurrentPlayer().nextPhase());

    }
  }

  //TODO EXPAND with CLAIMPHASE
  public void nextPlayer() {
    Player lastPlayer = players.poll();
    lastPlayer.nextPhase();
    Player nextPlayer = players.peek();
    gameState.setCurrentPlayer(nextPlayer);
    nextPlayer.nextPhase();
    nextPlayer.updateContinents(gameState.getContinents());
    gameState.setCurrentPhase(GamePhase.REINFORCEMENTPHASE);
    players.add(lastPlayer);

  }

  public ArrayList<Player> removeLostPlayers() {
    Queue<Player> players = gameState.getActivePlayers();
    ArrayList<Player> lostPlayers = new ArrayList<Player>();
    players.forEach(n -> {
          if (n.getCountries().size() == 0) {
            players.remove(n);
            lostPlayers.add(n);
          }
        }

    );
    return lostPlayers;
  }

  public void processAttack(Attack attack) {
    attack.calculateLosses();
    Country attackingCountry = attack.getAttackingCountry();
    attackingCountry.changeTroops(-attack.getAttackerLosses());
    Country defendingCountry = attack.getDefendingCountry();
    defendingCountry.changeTroops(-attack.getDefenderLosses());
    if (defendingCountry.getTroops() == 0) {
      Player player = attackingCountry.getPlayer();
      defendingCountry.setPlayer(player);
      player.addCountry(defendingCountry);
      //Forced Fortify after attack and takeover
      processFortify(
          new Fortify(attackingCountry, defendingCountry, attack.getTroopNumber()));
      //TODO ADDITIONAL MOVEMENT, if the Player chooses to do so
    }

  }

  public void processFortify(Fortify fortify) {
    fortify.getIncoming().changeTroops(fortify.getTroopsToMove());
    fortify.getOutgoing().changeTroops(-fortify.getTroopsToMove());

  }

  public void processReinforce(Reinforce reinforce) {
    if (gameState.getCurrentPhase().equals(GamePhase.CLAIMPHASE)) {
      reinforce.getCountry().setPlayer(gameState.getCurrentPlayer());
      gameState.getCurrentPlayer().addCountry(reinforce.getCountry());
      reinforce.getCountry().setTroops(1);

    } else {
      reinforce.getCountry().changeTroops(reinforce.getToAdd());
    }
  }


}