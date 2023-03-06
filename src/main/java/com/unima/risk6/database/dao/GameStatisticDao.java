package com.unima.risk6.database.dao;

import com.unima.risk6.database.models.GameStatistic;
import java.util.List;


/**
 * Data access object interface for Game Statistic model
 *
 * @author astoyano
 */
public interface GameStatisticDao extends Dao<GameStatistic> {

  List<GameStatistic> getAllStatisticsByUserId(Long id);

}
