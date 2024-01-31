package it.unipi.largescale.pixelindex.service;

import it.unipi.largescale.pixelindex.dto.GamePreviewDTO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;
import it.unipi.largescale.pixelindex.model.Game;

import java.util.List;

public interface GameService {
    List<GamePreviewDTO> searchGames(String name) throws ConnectionException;

    Game getGameById(String id) throws ConnectionException;

    void insertGameOnDocument(Game game) throws ConnectionException;

    void insertGameOnGraph(Game game) throws ConnectionException;

}
