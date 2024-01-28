package it.unipi.largescale.pixelindex.service;

import it.unipi.largescale.pixelindex.dto.GameSearchDTO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;
import it.unipi.largescale.pixelindex.model.Game;
import it.unipi.largescale.pixelindex.dao.GameDAO;
import java.util.List;

public interface GameService {
    List<GameSearchDTO> searchGames(String name) throws ConnectionException;
    Game getGameById(String id) throws ConnectionException;
}
