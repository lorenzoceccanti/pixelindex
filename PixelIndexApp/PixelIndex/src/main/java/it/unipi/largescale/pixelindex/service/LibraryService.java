package it.unipi.largescale.pixelindex.service;

import it.unipi.largescale.pixelindex.dto.GameDTO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;

import java.util.List;

public interface LibraryService {
    void addGame(String username, GameDTO game) throws ConnectionException;
    void removeGame(String username, GameDTO gameId) throws ConnectionException;
    List<GameDTO> getGames(String username) throws ConnectionException;
}
