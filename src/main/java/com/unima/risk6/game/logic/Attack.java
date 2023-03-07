package com.unima.risk6.game.logic;

import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.Player;
import java.util.ArrayList;

public class Attack {

  private Country toAttack;
  private Country toDefend;
  private int aLosses;
  private int dLosses;
  private ArrayList<Integer> aDice;
  private ArrayList<Integer> dDice;
  private int troopNumber;
  private Dice dice;
  private boolean isPossible;


  public Attack(Country attacking, Country defending, int attackingTroops) {
    this.toAttack = attacking;
    this.toDefend = defending;
    this.troopNumber = attackingTroops;
    this.aDice= new ArrayList<>();
    this.dDice= new ArrayList<>();

  }


  public void calculateLosses() {
    switch (troopNumber) {
      case 1:

        break;

      case 2:
        break;
      case 3:
        break;


    }

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

  public boolean isPossible() {
    return isPossible;
  }

  public int getTroopNumber() {
    return troopNumber;
  }
}
