package it.unipi.largescale.pixelindex.service.impl;

import it.unipi.largescale.pixelindex.dto.GameDTO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;
import it.unipi.largescale.pixelindex.exceptions.DAOException;
import it.unipi.largescale.pixelindex.service.LibraryService;
import it.unipi.largescale.pixelindex.dao.LibraryNeo4jDAO;

import java.util.List;

public class LibraryServiceImpl implements LibraryService {
    private final LibraryNeo4jDAO libraryNeo4jDAO;

    public LibraryServiceImpl() {
        libraryNeo4jDAO = new LibraryNeo4jDAO();
    }
    public void addGame(String username, GameDTO gameDTO) throws ConnectionException {
        try {
            libraryNeo4jDAO.addGame(username, gameDTO.getId());
        } catch (DAOException e) {
            throw new ConnectionException(e);
        }
    }

    public void removeGame(String username, GameDTO gameId) throws ConnectionException {
        try {
            libraryNeo4jDAO.removeGame(username, gameId.getId());
        } catch (DAOException e) {
            throw new ConnectionException(e);
        }
    }

    public List<GameDTO> getGames(String username) throws ConnectionException {
        try {
            return libraryNeo4jDAO.getGames(username);
        } catch (DAOException e) {
            throw new ConnectionException(e);
        }
    }
}
