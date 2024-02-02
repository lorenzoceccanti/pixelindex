package it.unipi.largescale.pixelindex.service.impl;

import it.unipi.largescale.pixelindex.dao.mongo.GameMongoDAO;
import it.unipi.largescale.pixelindex.dao.neo4j.GameNeo4jDAO;
import it.unipi.largescale.pixelindex.dto.GamePreviewDTO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;
import it.unipi.largescale.pixelindex.exceptions.DAOException;
import it.unipi.largescale.pixelindex.model.Game;
import it.unipi.largescale.pixelindex.service.GameService;
import it.unipi.largescale.pixelindex.utils.Utils;

import java.time.LocalDate;
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

    private GamePreviewDTO gamePreviewFromModel(Game game) {
        GamePreviewDTO gamePreview = new GamePreviewDTO();
        gamePreview.setId(game.getId());
        gamePreview.setName(game.getName());
        if(game.getReleaseDate() != null)
            gamePreview.setReleaseYear(game.getReleaseDate().getYear());
        return gamePreview;
    }

    public List<GamePreviewDTO> searchGames(String name) throws ConnectionException {
        try {
            List<Game> games = gameMongoDAO.getGamesByName(name);
            List<GamePreviewDTO> gamePreviews = new ArrayList<>();
            if(name.isEmpty()) return gamePreviews;

            for(Game g: games) {
                gamePreviews.add(gamePreviewFromModel(g));
            }
            return gamePreviews;
        } catch (DAOException e) {
            throw new ConnectionException(e);
        }
    }

    public List<GamePreviewDTO> advancedSearch(String searchSting) throws ConnectionException {
        try {
            List<GamePreviewDTO> gamePreviews = new ArrayList<>();
            Map<String, String> params = Utils.parseSearchString(searchSting);
            if(params.values().stream().noneMatch(value -> value != null && !value.isEmpty())) {
                return gamePreviews;
            }

            String name = params.getOrDefault("name", null);
            String company = params.getOrDefault("company", null);
            String platform = params.getOrDefault("platform", null);
            Integer releaseYear = params.containsKey("year") ? Integer.parseInt(params.get("year")) : null;

            List<Game> games = gameMongoDAO.getGamesAdvancedSearch(name, company, platform, releaseYear);
            for(Game g: games) {
                gamePreviews.add(gamePreviewFromModel(g));
            }
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

    public void insertGameOnGraph(Game game) throws ConnectionException {
        try {
            gameNeo4jDAO.insertGame(game);
        } catch (DAOException e) {
            throw new ConnectionException(e);
        }
    }

    public String insertGameOnDocument(Game game) throws ConnectionException {
        try {
            return gameMongoDAO.insertGame(game);
        } catch (DAOException e) {
            throw new ConnectionException(e);
        }
    }

}
