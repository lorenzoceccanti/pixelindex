package it.unipi.largescale.pixelindex.service.impl;

import it.unipi.largescale.pixelindex.controller.ConsistencyThread;
import it.unipi.largescale.pixelindex.dao.mongo.GameMongoDAO;
import it.unipi.largescale.pixelindex.dao.neo4j.GameNeo4jDAO;
import it.unipi.largescale.pixelindex.dto.GamePreviewDTO;
import it.unipi.largescale.pixelindex.dto.TrendingGamesDTO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;
import it.unipi.largescale.pixelindex.exceptions.DAOException;
import it.unipi.largescale.pixelindex.model.Game;
import it.unipi.largescale.pixelindex.service.GameService;
import it.unipi.largescale.pixelindex.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameServiceImpl implements GameService {
    private final GameMongoDAO gameMongoDAO;
    private final GameNeo4jDAO gameNeo4jDAO;

    public GameServiceImpl() {
        this.gameMongoDAO = new GameMongoDAO();
        this.gameNeo4jDAO = new GameNeo4jDAO();
    }

    public List<GamePreviewDTO> search(String searchSting, int page) throws ConnectionException {
        try {
            List<GamePreviewDTO> gamePreviews = new ArrayList<>();
            Map<String, String> params = Utils.parseSearchString(searchSting);
            if (params.values().stream().noneMatch(value -> value != null && !value.isEmpty())) {
                return gamePreviews;
            }

            String name = params.getOrDefault("name", null);
            String company = params.getOrDefault("company", null);
            String platform = params.getOrDefault("platform", null);
            Integer releaseYear = params.containsKey("year") ? Integer.parseInt(params.get("year")) : null;

            gamePreviews = gameMongoDAO.getGamesAdvancedSearch(name, company, platform, releaseYear, page);
            return gamePreviews;

        } catch (DAOException e) {
            throw new ConnectionException(e);
        }
    }

    public Game getGameById(String id) throws ConnectionException {
        try {
            return gameMongoDAO.getGameById(id);
        } catch (DAOException e) {
            throw new ConnectionException(e);
        }
    }

    public void insertGame(Game game, ConsistencyThread consistencyThread) throws ConnectionException {
        try {
            String gameId = gameMongoDAO.insertGame(game);
            game.setId(gameId);
            consistencyThread.addTask(() -> {
                try {
                    gameNeo4jDAO.insertGame(game);
                    gameMongoDAO.updateConsistencyFlag(game.getId());
                } catch (DAOException e) {
                    System.out.println("Consistency update failed while inserting game.");
                }
            });
        } catch (DAOException e) {
            throw new ConnectionException(e);
        }
    }

    public List<TrendingGamesDTO> getTrendingGames(Integer year, Integer limit) throws ConnectionException {
        try {
            return gameNeo4jDAO.getMostAddedToLibraryGames(year, limit);
        } catch (DAOException e) {
            throw new ConnectionException(e);
        }
    }
}
