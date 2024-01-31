package it.unipi.largescale.pixelindex.service;

import it.unipi.largescale.pixelindex.dto.GameDTO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;

import java.util.List;

public interface AnalyticsService {
    List<GameDTO> suggestGames(String username) throws ConnectionException;
    List<String> suggestUsers(String username) throws ConnectionException;
}
