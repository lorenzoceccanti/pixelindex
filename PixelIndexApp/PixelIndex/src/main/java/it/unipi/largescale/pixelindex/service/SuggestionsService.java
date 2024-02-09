package it.unipi.largescale.pixelindex.service;

import it.unipi.largescale.pixelindex.dto.GamePreviewDTO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;

import java.util.List;

public interface SuggestionsService {
    List<GamePreviewDTO> suggestGames(String username) throws ConnectionException;

    List<String> suggestUsers(String username) throws ConnectionException;
}
