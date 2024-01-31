package it.unipi.largescale.pixelindex.service.impl;

import it.unipi.largescale.pixelindex.dao.AnalyticsNeo4jDAO;
import it.unipi.largescale.pixelindex.dto.GamePreviewDTO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;
import it.unipi.largescale.pixelindex.exceptions.DAOException;
import it.unipi.largescale.pixelindex.service.AnalyticsService;

import java.util.List;

public class AnalyticsServiceImpl implements AnalyticsService {
    private AnalyticsNeo4jDAO analyticsNeo4jDAO;

    public AnalyticsServiceImpl() {
        this.analyticsNeo4jDAO = new AnalyticsNeo4jDAO();
    }

    public List<GamePreviewDTO> suggestGames(String username) throws ConnectionException {
        try {
            return analyticsNeo4jDAO.getSuggestedGames(username);
        } catch (DAOException e) {
            throw new ConnectionException(e);
        }
    }

    public List<String> suggestUsers(String username) throws ConnectionException {
        try {
            return analyticsNeo4jDAO.getSuggestedUsers(username);
        } catch (DAOException e) {
            throw new ConnectionException(e);
        }
    }
}
