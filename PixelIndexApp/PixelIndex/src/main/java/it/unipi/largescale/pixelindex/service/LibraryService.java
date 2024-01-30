package it.unipi.largescale.pixelindex.service;

import it.unipi.largescale.pixelindex.dto.GameDTO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;

public interface LibraryService {
    void addGameToLibrary(String username, GameDTO game) throws ConnectionException;
    void removeGameFromLibrary(String username, GameDTO gameId) throws ConnectionException;
}
