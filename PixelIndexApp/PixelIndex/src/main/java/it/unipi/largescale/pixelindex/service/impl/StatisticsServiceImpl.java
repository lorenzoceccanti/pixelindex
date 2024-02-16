package it.unipi.largescale.pixelindex.service.impl;

import it.unipi.largescale.pixelindex.dao.mongo.StatisticsMongoDAO;
import it.unipi.largescale.pixelindex.dto.GameRatingDTO;
import it.unipi.largescale.pixelindex.dto.MostActiveReviewerDTO;
import it.unipi.largescale.pixelindex.dto.RegistrationStatsDTO;
import it.unipi.largescale.pixelindex.dto.UserReportsDTO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;
import it.unipi.largescale.pixelindex.exceptions.DAOException;
import it.unipi.largescale.pixelindex.service.StatisticsService;

import java.util.ArrayList;

public class StatisticsServiceImpl implements StatisticsService {
    private final StatisticsMongoDAO statisticsMongoDAO;

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

    @Override
    public ArrayList<GameRatingDTO> topNRatedGames(int n) throws ConnectionException {
        try {
            return statisticsMongoDAO.topGamesByPositiveRatingRatio(n);
        } catch (DAOException ex) {
            throw new ConnectionException(ex);
        }
    }

    @Override
    public ArrayList<MostActiveReviewerDTO> findTop10ReviewersByReviewsCountLastMonth() throws ConnectionException {
        try {
            return statisticsMongoDAO.findTopReviewersByReviewsCountLastMonth(10);
        } catch (DAOException ex) {
            throw new ConnectionException(ex);
        }
    }

    @Override
    public ArrayList<RegistrationStatsDTO> numberOfRegistrationsByMonth(int year) throws ConnectionException {
        try {
            return statisticsMongoDAO.numberOfRegistrationsByMonth((long) year);
        } catch (DAOException ex) {
            throw new ConnectionException(ex);
        }
    }
}
