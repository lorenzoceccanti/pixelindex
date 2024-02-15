package it.unipi.largescale.pixelindex.service;

import it.unipi.largescale.pixelindex.dto.GameRatingDTO;
import it.unipi.largescale.pixelindex.dto.MostActiveReviewerDTO;
import it.unipi.largescale.pixelindex.dto.RegistrationStatsDTO;
import it.unipi.largescale.pixelindex.dto.UserReportsDTO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;

import java.util.ArrayList;

public interface StatisticsService {
    ArrayList<UserReportsDTO> topNReportedUser(int n) throws ConnectionException;

    ArrayList<GameRatingDTO> topNRatedGames(int n) throws ConnectionException;

    ArrayList<MostActiveReviewerDTO> findTop10ReviewersByReviewsCountLastMonth() throws ConnectionException;

    ArrayList<RegistrationStatsDTO> numberOfRegistrationsByMonth(int year) throws ConnectionException;
}
