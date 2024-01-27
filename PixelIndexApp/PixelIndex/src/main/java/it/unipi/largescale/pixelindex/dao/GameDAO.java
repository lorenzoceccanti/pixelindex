package it.unipi.largescale.pixelindex.dao;

import it.unipi.largescale.pixelindex.exceptions.DAOException;
import it.unipi.largescale.pixelindex.model.Game;

import java.util.List;

public interface GameDAO {

    Game getGameById(String id) throws DAOException;
    List<Game> getGamesByName(String name) throws DAOException;
}
