package it.unipi.largescale.pixelindex.service;

import it.unipi.largescale.pixelindex.controller.ConsistencyThread;
import it.unipi.largescale.pixelindex.dto.GamePreviewDTO;
import it.unipi.largescale.pixelindex.dto.TrendingGamesDTO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;
import it.unipi.largescale.pixelindex.model.Game;

import java.util.List;

public interface GameService {
    List<GamePreviewDTO> search(String name, int page) throws ConnectionException;

    Game getGameById(String id) throws ConnectionException;

    void insertGame(Game game, ConsistencyThread consistencyThread) throws ConnectionException;

    List<TrendingGamesDTO> getTrendingGames(Integer year, Integer limit) throws ConnectionException;

}
