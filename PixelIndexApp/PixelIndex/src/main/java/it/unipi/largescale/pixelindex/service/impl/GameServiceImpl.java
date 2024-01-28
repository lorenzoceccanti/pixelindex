package it.unipi.largescale.pixelindex.service.impl;

import it.unipi.largescale.pixelindex.dao.GameDAO;
import it.unipi.largescale.pixelindex.dao.GameMongoDAO;
import it.unipi.largescale.pixelindex.dto.GameSearchDTO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;
import it.unipi.largescale.pixelindex.exceptions.DAOException;
import it.unipi.largescale.pixelindex.model.Game;
import it.unipi.largescale.pixelindex.service.GameService;

import java.util.ArrayList;
import java.util.List;

public class GameServiceImpl implements GameService {
    private final GameDAO gameDAO;
    public GameServiceImpl() {
        this.gameDAO = new GameMongoDAO();
    }

    public List<GameSearchDTO> searchGames(String name) throws ConnectionException {
        List<Game> games = null;
        try {
            games = gameDAO.getGamesByName(name);
        } catch (DAOException e) {
            throw new ConnectionException(e);
        }

        List<GameSearchDTO> searchResult = new ArrayList<>();
        for(Game game : games) {
            GameSearchDTO gameSearchDTO = new GameSearchDTO();
            gameSearchDTO.setName(game.getName());
            gameSearchDTO.setReleaseDate(game.getReleaseDate());
            searchResult.add(gameSearchDTO);
        }
        return searchResult;
    }

    public Game getGameById(String id) throws ConnectionException {
        try {
            return gameDAO.getGameById(id);
        } catch (DAOException e) {
            throw new ConnectionException(e);
        }
    }

}
