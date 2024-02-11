package it.unipi.largescale.pixelindex.service;

import it.unipi.largescale.pixelindex.dto.GamePreviewDTO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;

import java.util.ArrayList;

public interface WishlistService {
    int addGame(String userId, String gameId) throws ConnectionException;

    int removeGame(String userId, String gameId) throws ConnectionException;

    ArrayList<GamePreviewDTO> getGames(String username, int page) throws ConnectionException;
}
