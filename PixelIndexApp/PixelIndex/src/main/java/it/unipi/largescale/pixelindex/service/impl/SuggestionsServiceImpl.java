package it.unipi.largescale.pixelindex.service.impl;

import it.unipi.largescale.pixelindex.dao.neo4j.SuggestionsNeo4jDAO;
import it.unipi.largescale.pixelindex.dto.GamePreviewDTO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;
import it.unipi.largescale.pixelindex.exceptions.DAOException;
import it.unipi.largescale.pixelindex.service.SuggestionsService;

import java.util.List;

public class SuggestionsServiceImpl implements SuggestionsService {
    private final SuggestionsNeo4jDAO suggestionsNeo4JDAO;

    public SuggestionsServiceImpl() {
        this.suggestionsNeo4JDAO = new SuggestionsNeo4jDAO();
    }

    public List<GamePreviewDTO> suggestGames(String username) throws ConnectionException {
        try {
            return suggestionsNeo4JDAO.getSuggestedGames(username);
        } catch (DAOException e) {
            throw new ConnectionException(e);
        }
    }

    public List<String> suggestUsers(String username) throws ConnectionException {
        try {
            return suggestionsNeo4JDAO.getSuggestedUsers(username);
        } catch (DAOException e) {
            throw new ConnectionException(e);
        }
    }
}
