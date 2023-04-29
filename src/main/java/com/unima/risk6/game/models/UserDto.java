package com.unima.risk6.game.models;

public class UserDto {

  private final String username;
  private final double winLossRatio;

  private final double hoursPlayed;

  private final int gamesWon;

  private final int gamesLost;

  private final int countriesConquered;

  public UserDto(String username, double winLossRatio, double hoursPlayed, int gamesWon,
      int gamesLost, int countriesConquered) {
    this.username = username;
    this.winLossRatio = winLossRatio;
    this.hoursPlayed = hoursPlayed;
    this.gamesWon = gamesWon;
    this.gamesLost = gamesLost;
    this.countriesConquered = countriesConquered;
  }

  public String getUsername() {
    return username;
  }

  public double getWinLossRatio() {
    return winLossRatio;
  }

  public double getHoursPlayed() {
    return hoursPlayed;
  }

  public int getGamesWon() {
    return gamesWon;
  }

  public int getGamesLost() {
    return gamesLost;
  }

  public int getCountriesConquered() {
    return countriesConquered;
  }
}
