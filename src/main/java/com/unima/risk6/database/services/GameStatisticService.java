package com.unima.risk6.database.services;

import com.unima.risk6.database.exceptions.NotFoundException;
import com.unima.risk6.database.models.GameStatistic;
import com.unima.risk6.database.repositories.GameStatisticRepository;
import java.time.LocalDateTime;
import java.util.List;

public class GameStatisticService {

  private final GameStatisticRepository gameStatisticRepository;

  public GameStatisticService(GameStatisticRepository gameStatisticRepository) {
    this.gameStatisticRepository = gameStatisticRepository;
  }

  public GameStatistic getGameStatisticById(Long id) {
    return gameStatisticRepository.get(id)
        .orElseThrow(
            () -> new NotFoundException(
                "Game Statistic with id {" + id + "} is not in the database"));
  }

  public List<GameStatistic> getAllGameStatistics() {
    return gameStatisticRepository.getAll();
  }

  public void updateGameStatisticAfterGame(GameStatistic gameStatistic) {
    gameStatisticRepository.update(gameStatistic);
  }

  public Long saveGameStatistic(GameStatistic gameStatistic) {
    gameStatistic.setStartDate(LocalDateTime.now());
    return gameStatisticRepository.save(gameStatistic);
  }

}
