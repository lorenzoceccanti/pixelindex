package it.unipi.largescale.pixelindex.service;

import it.unipi.largescale.pixelindex.dto.GamePreviewDTO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;

import java.util.List;

public interface LibraryService {
    void addGame(String username, GamePreviewDTO game) throws ConnectionException;

    void removeGame(String username, GamePreviewDTO gameId) throws ConnectionException;

    List<GamePreviewDTO> getGames(String username) throws ConnectionException;
}
