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

  private boolean gameWon;

  public GameStatistic() {
  }

  public GameStatistic(Long id, User user, LocalDateTime startDate, LocalDateTime finishDate,
      int troopsLost, int troopsGained, boolean gameWon) {
    this.id = id;
    this.user = user;
    this.startDate = startDate;
    this.finishDate = finishDate;
    this.troopsLost = troopsLost;
    this.troopsGained = troopsGained;
    this.gameWon = gameWon;
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

  @Override
  public String toString() {
    return "GameStatistic{" +
           "id=" + id +
           ", user=" + user +
           ", startDate=" + startDate +
           ", finishDate=" + finishDate +
           ", troopsLost=" + troopsLost +
           ", troopsGained=" + troopsGained +
           ", won=" + gameWon +
           '}';
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
