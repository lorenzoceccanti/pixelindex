package it.unipi.largescale.pixelindex.service.impl;

import it.unipi.largescale.pixelindex.dao.mongo.StatisticsMongoDAO;
import it.unipi.largescale.pixelindex.dto.UserReportsDTO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;
import it.unipi.largescale.pixelindex.exceptions.DAOException;
import it.unipi.largescale.pixelindex.service.StatisticsService;

import java.util.ArrayList;

public class StatisticsServiceImpl implements StatisticsService {
    private StatisticsMongoDAO statisticsMongoDAO;

    public StatisticsServiceImpl() {
        this.statisticsMongoDAO = new StatisticsMongoDAO();
    }

    @Override
    public ArrayList<UserReportsDTO> topNReportedUser(int n) throws ConnectionException {
        try {
            return statisticsMongoDAO.topNReportedUser(n);
        } catch (DAOException ex) {
            throw new ConnectionException(ex);
        }
    }
}
