package it.unipi.largescale.pixelindex.service;

import it.unipi.largescale.pixelindex.dto.GameDTO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;

import java.util.List;

public interface WishlistService {
    void addGame(String userId, GameDTO game) throws ConnectionException;
    void removeGame(String userId, GameDTO gameId) throws ConnectionException;
    List<GameDTO> getGames(String userId) throws ConnectionException;
}
