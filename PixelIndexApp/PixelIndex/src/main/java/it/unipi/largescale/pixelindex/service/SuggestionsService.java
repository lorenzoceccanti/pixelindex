package it.unipi.largescale.pixelindex.service;

import it.unipi.largescale.pixelindex.dto.GamePreviewDTO;
import it.unipi.largescale.pixelindex.dto.GameSuggestionDTO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;

import java.util.List;

public interface SuggestionsService {
    List<GameSuggestionDTO> suggestGames(String username) throws ConnectionException;

    List<String> suggestUsers(String username) throws ConnectionException;
}
