package it.unipi.largescale.pixelindex.service.impl;

import it.unipi.largescale.pixelindex.dao.neo4j.WishlistNeo4jDAO;
import it.unipi.largescale.pixelindex.dto.GamePreviewDTO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;
import it.unipi.largescale.pixelindex.exceptions.DAOException;
import it.unipi.largescale.pixelindex.service.WishlistService;

import java.util.ArrayList;

public class WishlistServiceImpl implements WishlistService {
    private final WishlistNeo4jDAO wishlistNeo4jDAO;

    public WishlistServiceImpl() {
        wishlistNeo4jDAO = new WishlistNeo4jDAO();
    }

    public void addGame(String userId, String gameId) throws ConnectionException {
        try {
            wishlistNeo4jDAO.insertGame(userId, gameId);
        } catch (DAOException e) {
            throw new ConnectionException(e);
        }
    }

    public void removeGame(String userId, String gameId) throws ConnectionException {
        try {
            wishlistNeo4jDAO.removeGame(userId, gameId);
        } catch (DAOException e) {
            throw new ConnectionException(e);
        }
    }

    public ArrayList<GamePreviewDTO> getGames(String username) throws ConnectionException {
        try {
            return wishlistNeo4jDAO.getGames(username);
        } catch (DAOException e) {
            throw new ConnectionException(e);
        }
    }
}


