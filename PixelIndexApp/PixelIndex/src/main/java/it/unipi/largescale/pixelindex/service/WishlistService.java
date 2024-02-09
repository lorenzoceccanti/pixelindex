package it.unipi.largescale.pixelindex.service;

import it.unipi.largescale.pixelindex.dto.GamePreviewDTO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;

import java.util.ArrayList;
import java.util.List;

public interface WishlistService {
    void addGame(String userId, String gameId) throws ConnectionException;

    void removeGame(String userId, String gameId) throws ConnectionException;

    ArrayList<GamePreviewDTO> getGames(String username, int page) throws ConnectionException;
}
