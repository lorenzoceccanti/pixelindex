package it.unipi.largescale.pixelindex.dao;

import it.unipi.largescale.pixelindex.exceptions.DAOException;
import it.unipi.largescale.pixelindex.model.Game;

import java.util.List;

public interface GameMongoDAO {

    Game getGameById(String id) throws DAOException;
    List<Game> getGamesByName(String name) throws DAOException;

    void insertGame(Game game) throws DAOException;
}
