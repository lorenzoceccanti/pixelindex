package it.unipi.largescale.pixelindex.service;

import it.unipi.largescale.pixelindex.dto.GamePreviewDTO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;

import java.util.List;

public interface WishlistService {
    void addGame(String userId, GamePreviewDTO game) throws ConnectionException;

    void removeGame(String userId, GamePreviewDTO gameId) throws ConnectionException;

    List<GamePreviewDTO> getGames(String userId) throws ConnectionException;
}
