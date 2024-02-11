package it.unipi.largescale.pixelindex.service;

import it.unipi.largescale.pixelindex.dto.GameRatingDTO;
import it.unipi.largescale.pixelindex.dto.MostActiveUserDTO;
import it.unipi.largescale.pixelindex.dto.UserReportsDTO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;

import java.util.ArrayList;

public interface StatisticsService {
    ArrayList<UserReportsDTO> topNReportedUser(int n) throws ConnectionException;
    ArrayList<GameRatingDTO> topNRatedGames(int n) throws ConnectionException;
    ArrayList<MostActiveUserDTO> findTop10ReviewersByPostCountLastMonth() throws ConnectionException;
}
