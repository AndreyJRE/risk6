package com.unima.risk6.game.models;

import com.unima.risk6.database.models.GameStatistic;
import com.unima.risk6.database.models.User;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

/**
 * User data transfer object. Contains all the information about a user and his game statistics.
 *
 * @author astoyano
 */
public class UserDto {

  private String username;
  private final double winLossRatio;

  private final double hoursPlayed;

  private final int gamesWon;

  private final int gamesLost;

  private final int countriesConquered;

  /**
   * Constructor for a user data transfer object. Creates a new user data transfer object with the
   * given username, win loss ratio, hours played, games won, games lost and countries conquered.
   *
   * @param username           The username.
   * @param winLossRatio       The win loss ratio.
   * @param hoursPlayed        The hours played.
   * @param gamesWon           The games won.
   * @param gamesLost          The games lost.
   * @param countriesConquered The countries conquered.
   */
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

  /**
   * Maps a user and his game statistics to a user data transfer object. Creates a new user data
   * transfer object with the given user and his game statistics.
   *
   * @param user              The user.
   * @param userGameStatistic The user game statistics.
   * @return The user data transfer object.
   */
  public static UserDto mapUserAndHisGameStatistics(User user,
      List<GameStatistic> userGameStatistic) {
    if (userGameStatistic.isEmpty()) {
      return new UserDto(user.getUsername(), 0, 0, 0, 0, 0);
    }
    int won = (int) userGameStatistic.stream().filter(GameStatistic::isGameWon).count();
    int lost = userGameStatistic.size() - won;
    if (lost == 0) {
      lost += 1;
    }
    double lossRatio = ((double) won / userGameStatistic.size()) * 10;
    double hoursPlayed = userGameStatistic.stream().mapToDouble(g -> {
      Duration duration = Duration.between(g.getStartDate(), g.getFinishDate());
      return duration.toSeconds() / 3600.0;
    }).sum();
    int countriesConquered = userGameStatistic.stream().mapToInt(GameStatistic::getCountriesWon)
        .sum();
    return new UserDto(user.getUsername(), lossRatio, hoursPlayed, won, lost, countriesConquered);
  }

  @Override
  public String toString() {
    return "UserDto{" + "username='" + username + '\'' + ", winLossRatio=" + winLossRatio
        + ", hoursPlayed=" + hoursPlayed + ", gamesWon=" + gamesWon + ", gamesLost=" + gamesLost
        + ", countriesConquered=" + countriesConquered + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserDto userDto = (UserDto) o;
    return Objects.equals(username, userDto.username);
  }

  public void setUsername(String username) {
    this.username = username;
  }

}
