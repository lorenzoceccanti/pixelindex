package it.unipi.largescale.pixelindex.service.impl;

import it.unipi.largescale.pixelindex.dao.LibraryNeo4jDAO;
import it.unipi.largescale.pixelindex.dao.WishlistMongoDAO;
import it.unipi.largescale.pixelindex.dto.GameDTO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;
import it.unipi.largescale.pixelindex.exceptions.DAOException;
import it.unipi.largescale.pixelindex.service.WishlistService;

public class WishlistServiceImpl implements WishlistService {
    private final WishlistMongoDAO wishlistMongoDAO;

    public WishlistServiceImpl() {
        wishlistMongoDAO = new WishlistMongoDAO();
    }
    public void addGameToWishlist(String userId, GameDTO game) throws ConnectionException {
        try {
            wishlistMongoDAO.insertWishlist(userId, game);
        } catch (DAOException e) {
            throw new ConnectionException(e);
        }
    }
    public void removeGameFromWishlist(String userId, GameDTO game) throws ConnectionException {}
}
