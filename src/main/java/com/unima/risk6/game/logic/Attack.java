package com.unima.risk6.game.logic;

import com.unima.risk6.game.models.Country;
import java.util.ArrayList;
import java.util.Collections;

public class Attack extends Move {

  private final Country attackingCountry;
  private final Country defendingCountry;
  private int attackerLosses;
  private int defenderLosses;
  private ArrayList<Integer> attackDiceResult;
  private ArrayList<Integer> defendDiceResult;
  private final int troopNumber;


  public Attack(Country attackingCountry, Country defendingCountry, int troopNumber) {
    this.attackingCountry = attackingCountry;
    this.defendingCountry = defendingCountry;
    this.troopNumber = troopNumber;
    this.attackDiceResult = new ArrayList<Integer>();
    this.defendDiceResult = new ArrayList<Integer>();
    attackerLosses = 0;
    defenderLosses = 0;
  }

  public void calculateLosses() {

    switch (troopNumber) {
      case 1:
        attackDiceResult.add(Dice.rollDice());
        defendDiceResult.add(Dice.rollDice());
        sortDicelist();
        compareDice(0);
        break;

      case 2:
        for (int i = 0; i < 2; i++) {
          attackDiceResult.add(Dice.rollDice());
        }
        defendDiceResult.add(Dice.rollDice());
        if (defendingCountry.getTroops() > 1) {
          defendDiceResult.add(Dice.rollDice());
        }
        sortDicelist();
        if (defendingCountry.getTroops() > 1) {
          for (int i = 0; i < 2; i++) {
            compareDice(i);
          }
        } else {
          compareDice(0);
        }

        break;
      case 3:

        for (int i = 0; i < 3; i++) {
          attackDiceResult.add(Dice.rollDice());
        }
        defendDiceResult.add(Dice.rollDice());
        if (defendingCountry.getTroops() > 1) {
          defendDiceResult.add(Dice.rollDice());
        }
        sortDicelist();

        if (defendingCountry.getTroops() > 1) {
          for (int i = 0; i < 2; i++) {
            compareDice(i);
          }
        } else {
          compareDice(0);
        }
        break;
      default:
        break;
    }

  }


  public void compareDice(int n) {
    if (attackDiceResult.get(n) > defendDiceResult.get(n)) {
      defenderLosses++;
    } else {
      attackerLosses++;
    }
  }

  public void sortDicelist() {
    Collections.sort(defendDiceResult, (x, y) -> y - x);
    Collections.sort(attackDiceResult, (x, y) -> y - x);
  }

  public ArrayList<Integer> getAttackDiceResult() {
    return attackDiceResult;
  }

  public ArrayList<Integer> getdDice() {
    return defendDiceResult;
  }


  public Country getAttackingCountry() {
    return attackingCountry;
  }

  public Country getDefendingCountry() {
    return defendingCountry;
  }

  public int getAttackerLosses() {
    return attackerLosses;
  }

  public int getDefenderLosses() {
    return defenderLosses;
  }

  public int getTroopNumber() {
    return troopNumber;
  }
}
