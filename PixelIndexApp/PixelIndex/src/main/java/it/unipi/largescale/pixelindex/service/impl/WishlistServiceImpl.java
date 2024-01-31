package it.unipi.largescale.pixelindex.service.impl;

import it.unipi.largescale.pixelindex.dao.LibraryNeo4jDAO;
import it.unipi.largescale.pixelindex.dao.WishlistMongoDAO;
import it.unipi.largescale.pixelindex.dto.GameDTO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;
import it.unipi.largescale.pixelindex.exceptions.DAOException;
import it.unipi.largescale.pixelindex.model.Game;
import it.unipi.largescale.pixelindex.service.WishlistService;

import java.util.List;

public class WishlistServiceImpl implements WishlistService {
    private final WishlistMongoDAO wishlistMongoDAO;

    public WishlistServiceImpl() {
        wishlistMongoDAO = new WishlistMongoDAO();
    }
    public void addGame(String userId, GameDTO game) throws ConnectionException {
        try {
            wishlistMongoDAO.insertGame(userId, game);
        } catch (DAOException e) {
            throw new ConnectionException(e);
        }
    }
    public void removeGame(String userId, GameDTO game) throws ConnectionException {
        try {
            wishlistMongoDAO.removeGame(userId, game.getId());
        } catch (DAOException e) {
            throw new ConnectionException(e);
        }
    }

    public List<GameDTO> getGames(String userId) throws ConnectionException {
        try {
            return wishlistMongoDAO.getGames(userId);
        } catch (DAOException e) {
            throw new ConnectionException(e);
        }
    }
}

