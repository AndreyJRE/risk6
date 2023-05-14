package com.unima.risk6.database.services;

import com.unima.risk6.database.exceptions.NotFoundException;
import com.unima.risk6.database.models.GameStatistic;
import com.unima.risk6.database.repositories.GameStatisticRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The GameStatisticService class is a service class for the GameStatistic model. It contains
 * methods that are used to retrieve, update and save GameStatistic objects. It uses the
 * GameStatisticRepository class to communicate with the database.
 *
 * @author astoyano
 */
public class GameStatisticService {

  private final GameStatisticRepository gameStatisticRepository;
  private static final Logger LOGGER = LoggerFactory.getLogger(GameStatisticService.class);

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
    return gameStatisticRepository.get(id).orElseThrow(
        () -> new NotFoundException("Game Statistic with id {" + id + "} is not in the database"));
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
    Optional<GameStatistic> gameStatisticOptional = gameStatisticRepository.get(
        gameStatistic.getId());
    if (gameStatisticOptional.isEmpty()) {
      throw new NotFoundException(
          "Game Statistic with id {" + gameStatistic.getId() + "} is not in the database");
    }
    gameStatistic.setFinishDate(LocalDateTime.now());
    gameStatisticRepository.update(gameStatistic);
    LOGGER.info("Updating game statistic with id {}", gameStatistic.getId());

  }

  /**
   * Saves a GameStatistic object in the database.
   *
   * @param gameStatistic The GameStatistic object to save.
   */
  public void saveGameStatistic(GameStatistic gameStatistic) {
    gameStatistic.setStartDate(LocalDateTime.now());
    gameStatisticRepository.save(gameStatistic);
    LOGGER.info("Saving game statistic with id {}", gameStatistic.getId());
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

  /**
   * Closes the connection to the repository.
   */
  public void close() {
    try {
      gameStatisticRepository.closeStatements();
    } catch (Exception e) {
      LOGGER.error("Error closing connection to database", e);
    }
  }
}
