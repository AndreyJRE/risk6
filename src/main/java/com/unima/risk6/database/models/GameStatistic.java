package com.unima.risk6.database.models;


import java.time.LocalDateTime;
import java.util.Objects;

/**
 * A game statistic model from one game for database
 *
 * @author astoyano
 */
public class GameStatistic {

  private Long id;

  private User user;

  private LocalDateTime startDate;

  private LocalDateTime finishDate;

  private int troopsLost;

  private int troopsGained;

  private int countriesWon;

  private int countriesLost;

  private boolean gameWon;

  /**
   * Default constructor.
   */
  public GameStatistic() {
  }

  /**
   * Constructor for a game statistic. This constructor is used when a game is started. The finish
   * date and game won are not known yet.
   *
   * @param user The user that is playing the game.
   */
  public GameStatistic(User user) {
    this.user = user;
  }

  /**
   * Constructor for a game statistic. This constructor is used when a game is finished. The finish
   * date and game won are known.
   *
   * @param id            The ID of the game statistic.
   * @param user          The user that is playing the game.
   * @param startDate     The date and time the game was started.
   * @param finishDate    The date and time the game was finished.
   * @param troopsLost    The number of troops lost during the game.
   * @param troopsGained  The number of troops gained during the game.
   * @param gameWon       Whether the game was won or not.
   * @param countriesWon  The number of countries won during the game.
   * @param countriesLost The number of countries lost during the game.
   */
  public GameStatistic(Long id, User user, LocalDateTime startDate, LocalDateTime finishDate,
      int troopsLost, int troopsGained, boolean gameWon, int countriesWon, int countriesLost) {
    this.id = id;
    this.user = user;
    this.startDate = startDate;
    this.finishDate = finishDate;
    this.troopsLost = troopsLost;
    this.troopsGained = troopsGained;
    this.gameWon = gameWon;
    this.countriesWon = countriesWon;
    this.countriesLost = countriesLost;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public LocalDateTime getStartDate() {
    return startDate;
  }

  public void setStartDate(LocalDateTime startDate) {
    this.startDate = startDate;
  }

  public LocalDateTime getFinishDate() {
    return finishDate;
  }

  public void setFinishDate(LocalDateTime finishDate) {
    this.finishDate = finishDate;
  }

  public int getTroopsLost() {
    return troopsLost;
  }

  public void setTroopsLost(int troopsLost) {
    this.troopsLost = troopsLost;
  }

  public int getTroopsGained() {
    return troopsGained;
  }


  public void setTroopsGained(int troopsGained) {
    this.troopsGained = troopsGained;
  }

  public boolean isGameWon() {
    return gameWon;
  }

  public void setGameWon(boolean gameWon) {
    this.gameWon = gameWon;
  }

  public int getCountriesWon() {
    return countriesWon;
  }

  public void setCountriesWon(int countriesWon) {
    this.countriesWon = countriesWon;
  }

  public int getCountriesLost() {
    return countriesLost;
  }

  public void setCountriesLost(int countriesLost) {
    this.countriesLost = countriesLost;
  }

  @Override
  public String toString() {
    return "GameStatistic{" + "id=" + id + ", user=" + user + ", startDate=" + startDate
        + ", finishDate=" + finishDate + ", troopsLost=" + troopsLost + ", troopsGained="
        + troopsGained + ", countriesWon=" + countriesWon + ", countriesLost=" + countriesLost
        + ", gameWon=" + gameWon + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GameStatistic that = (GameStatistic) o;
    return troopsLost == that.troopsLost && troopsGained == that.troopsGained
        && gameWon == that.gameWon && id.equals(that.id) && user.equals(that.user)
        && startDate.equals(that.startDate) && Objects.equals(finishDate,
        that.finishDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, user, startDate, finishDate, troopsLost, troopsGained, gameWon);
  }
}
