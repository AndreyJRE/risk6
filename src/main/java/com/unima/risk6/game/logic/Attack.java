package com.unima.risk6.game.logic;

import com.unima.risk6.game.models.Country;
import java.util.ArrayList;
import java.util.Collections;

public class Attack extends Move {

  private Country toAttack;
  private Country toDefend;
  private int aLosses;
  private int dLosses;
  private ArrayList<Integer> attackDice;
  private ArrayList<Integer> defendDice;
  private int troopNumber;
  private Dice dice;

  public Attack(Country toAttack, Country toDefend, int troopNumber) {
    this.toAttack = toAttack;
    this.toDefend = toDefend;
    this.troopNumber = troopNumber;
    this.attackDice = new ArrayList<Integer>();
    this.defendDice = new ArrayList<Integer>();
    this.dice = new Dice();
    aLosses = 0;
    dLosses = 0;
  }


  public void calculateLosses() {

    switch (troopNumber) {
      case 1:
        attackDice.add(dice.rollDice());
        defendDice.add(dice.rollDice());
        sortDicelist();
        compareDice(0);
        break;

      case 2:
        for (int i = 0; i < 2; i++) {
          attackDice.add(dice.rollDice());
        }
        defendDice.add(dice.rollDice());
        if (toDefend.getTroops() > 1) {
          defendDice.add(dice.rollDice());
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
          attackDice.add(dice.rollDice());
        }
        defendDice.add(dice.rollDice());
        if (toDefend.getTroops() > 1) {
          defendDice.add(dice.rollDice());
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
    if (attackDice.get(n) > defendDice.get(n)) {
      dLosses++;
    } else {
      aLosses++;
    }
  }

  public void sortDicelist() {
    Collections.sort(defendDice, (x, y) -> y - x);
    Collections.sort(attackDice, (x, y) -> y - x);
  }

  public ArrayList<Integer> getAttackDice() {
    return attackDice;
  }

  public ArrayList<Integer> getdDice() {
    return defendDice;
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
