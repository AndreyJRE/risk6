package com.unima.risk6.database.models;


import java.time.LocalDate;

/**
 * A game statistic model from one game for database
 *
 * @author astoyano
 */
public class GameStatistic {

  private Long id;

  private User user;

  private LocalDate startDate;

  private LocalDate finishDate;

  private int armyLoses;

  private int armyWon;

  private boolean won;


}
