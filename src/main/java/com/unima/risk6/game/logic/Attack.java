package com.unima.risk6.game.logic;

import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.Player;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Attack extends Move {

  private Country toAttack;
  private Country toDefend;
  private int aLosses;
  private int dLosses;
  private ArrayList<Integer> aDice;
  private ArrayList<Integer> dDice;
  private int troopNumber;
  private Dice dice;

  public Attack(Country attacking, Country defending, int attackingTroops) {
    this.toAttack = attacking;
    this.toDefend = defending;
    this.troopNumber = attackingTroops;
    this.aDice = new ArrayList<>();
    this.dDice = new ArrayList<>();
    aLosses = 0;
    dLosses = 0;
    calculateLosses();
  }


  public void calculateLosses() {

    switch (troopNumber) {
      case 1:
        aDice.add(dice.rollDice());
        dDice.add(dice.rollDice());
        sortDicelist();
        compareDice(0);
        break;

      case 2:
        for (int i = 0; i < 2; i++) {
          aDice.add(dice.rollDice());
        }
        dDice.add(dice.rollDice());
        if (toDefend.getTroops() > 1) {
          dDice.add(dice.rollDice());
        }
        sortDicelist();
        if (toDefend.getTroops() > 1) {
          for (int i = 0; i < 2; i++) {
            compareDice(i);
          }
        } else {
          compareDice(0);
        }

        break;
      case 3:

        for (int i = 0; i < 3; i++) {
          aDice.add(dice.rollDice());
        }
        dDice.add(dice.rollDice());
        if (toDefend.getTroops() > 1) {
          dDice.add(dice.rollDice());
        }
        sortDicelist();

        if (toDefend.getTroops() > 1) {
          for (int i = 0; i < 2; i++) {
            compareDice(i);
          }
        } else {
          compareDice(0);
        }
        break;
    }

  }


  public void compareDice(int n) {
    if (aDice.get(n) > dDice.get(n)) {
      dLosses++;
    } else {
      aLosses++;
    }
  }

  public void sortDicelist() {
    Collections.sort(dDice, (x, y) -> y - x);
    Collections.sort(aDice, (x, y) -> y - x);
  }

  public ArrayList<Integer> getaDice() {
    return aDice;
  }

  public ArrayList<Integer> getdDice() {
    return dDice;
  }


  public Country getToAttack() {
    return toAttack;
  }

  public Country getToDefend() {
    return toDefend;
  }

  public int getaLosses() {
    return aLosses;
  }

  public int getdLosses() {
    return dLosses;
  }

  public int getTroopNumber() {
    return troopNumber;
  }
}
