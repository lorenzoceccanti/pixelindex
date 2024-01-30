package it.unipi.largescale.pixelindex.service.impl;

import it.unipi.largescale.pixelindex.dto.GameDTO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;
import it.unipi.largescale.pixelindex.exceptions.DAOException;
import it.unipi.largescale.pixelindex.service.LibraryService;
import it.unipi.largescale.pixelindex.dao.LibraryNeo4jDAO;
public class LibraryServiceImpl implements LibraryService {
    private final LibraryNeo4jDAO libraryNeo4jDAO;

    public LibraryServiceImpl() {
        libraryNeo4jDAO = new LibraryNeo4jDAO();
    }
    public void addGameToLibrary(String username, GameDTO gameDTO) throws ConnectionException {
        try {
            libraryNeo4jDAO.insertAddsToLibrary(username, gameDTO.getId());
        } catch (DAOException e) {
            throw new ConnectionException(e);
        }
    }

    public void removeGameFromLibrary(String username, GameDTO gameId) throws ConnectionException {
        try {
            libraryNeo4jDAO.deleteAddsToLibrary(username, gameId.getId());
        } catch (DAOException e) {
            throw new ConnectionException(e);
        }

    }
}
