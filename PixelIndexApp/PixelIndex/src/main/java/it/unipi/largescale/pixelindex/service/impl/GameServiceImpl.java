package it.unipi.largescale.pixelindex.service.impl;

import it.unipi.largescale.pixelindex.dao.GameDAO;
import it.unipi.largescale.pixelindex.dao.GameMongoDAO;
import it.unipi.largescale.pixelindex.dto.SearchGameDTO;
import it.unipi.largescale.pixelindex.exceptions.DAOException;
import it.unipi.largescale.pixelindex.model.Game;

import java.util.ArrayList;
import java.util.List;

public class GameServiceImpl {
    private final GameDAO gameDAO;
    public GameServiceImpl(){
        this.gameDAO = new GameMongoDAO();
    }

    public List<SearchGameDTO> searchGame(String name) throws DAOException {
        List<Game> games = gameDAO.getGamesByName(name);
        List<SearchGameDTO> searchResult = new ArrayList<>();
        for(Game game : games) {
            SearchGameDTO searchGameDTO = new SearchGameDTO();
            searchGameDTO.setName(game.getName());
            searchGameDTO.setReleaseDate(game.getReleaseDate());
            searchResult.add(searchGameDTO);
        }
        return searchResult;
    }

}
