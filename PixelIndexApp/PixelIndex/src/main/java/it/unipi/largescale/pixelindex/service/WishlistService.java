package it.unipi.largescale.pixelindex.service;

import it.unipi.largescale.pixelindex.dto.GameDTO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;

public interface WishlistService {
    void addGameToWishlist(String userId, GameDTO game) throws ConnectionException;
    void removeGameFromWishlist(String userId, GameDTO gameId) throws ConnectionException;
}
