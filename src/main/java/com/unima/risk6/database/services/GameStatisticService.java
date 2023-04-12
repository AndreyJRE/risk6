package com.unima.risk6.database.services;

import com.unima.risk6.database.exceptions.NotFoundException;
import com.unima.risk6.database.models.GameStatistic;
import com.unima.risk6.database.repositories.GameStatisticRepository;
import java.time.LocalDateTime;
import java.util.List;

/**
 * The GameStatisticService class is a service class for the GameStatistic model. It contains
 * methods that are used to retrieve, update and save GameStatistic objects. It uses the
 * GameStatisticRepository class to communicate with the database.
 *
 * @author astoyano
 */
public class GameStatisticService {

  private final GameStatisticRepository gameStatisticRepository;

  /**
   * Constructs a new GameStatisticService object with the specified GameStatisticRepository.
   *
   * @param gameStatisticRepository The GameStatisticRepository to use.
   */
  public GameStatisticService(GameStatisticRepository gameStatisticRepository) {
    this.gameStatisticRepository = gameStatisticRepository;
  }

  /**
   * Retrieves a GameStatistic object with the specified ID. If no GameStatistic object is found, a
   * NotFoundException is thrown.
   *
   * @param id The ID of the GameStatistic object to retrieve.
   * @return The GameStatistic object with the specified ID.
   * @throws NotFoundException If no GameStatistic object is found with the specified ID.
   */
  public GameStatistic getGameStatisticById(Long id) {
    return gameStatisticRepository.get(id)
        .orElseThrow(
            () -> new NotFoundException(
                "Game Statistic with id {" + id + "} is not in the database"));
  }

  /**
   * Retrieves a list of all GameStatistic objects. If no GameStatistic objects are found, an empty
   * list is returned.
   *
   * @return A list of all GameStatistic objects.
   */
  public List<GameStatistic> getAllGameStatistics() {
    return gameStatisticRepository.getAll();
  }

  /**
   * Retrieves a list of all GameStatistic objects associated with a specified user ID.
   *
   * @param userId The user ID to retrieve game statistics for.
   * @return A list of GameStatistic objects associated with the specified user ID.
   */
  public List<GameStatistic> getAllStatisticsByUserId(Long userId) {
    return gameStatisticRepository.getAllStatisticsByUserId(userId);
  }

  /**
   * Updates a GameStatistic object in the database.
   *
   * @param gameStatistic The GameStatistic object to update.
   */
  public void updateGameStatisticAfterGame(GameStatistic gameStatistic) {
    gameStatisticRepository.update(gameStatistic);
  }

  /**
   * Saves a GameStatistic object in the database.
   *
   * @param gameStatistic The GameStatistic object to save.
   * @return The ID of the saved GameStatistic object.
   */
  public Long saveGameStatistic(GameStatistic gameStatistic) {
    gameStatistic.setStartDate(LocalDateTime.now());
    return gameStatisticRepository.save(gameStatistic);
  }

  /**
   * Retrieves a list of all GameStatistic objects associated with a specified gameWon value.
   *
   * @param gameWon boolean value to check if game is won or lost
   * @return A list of GameStatistic objects associated with the specified gameWon value.
   */
  public List<GameStatistic> getAllStatisticsByGameWon(boolean gameWon) {
    return gameStatisticRepository.getAllStatisticsByGameWon(gameWon);
  }

}
