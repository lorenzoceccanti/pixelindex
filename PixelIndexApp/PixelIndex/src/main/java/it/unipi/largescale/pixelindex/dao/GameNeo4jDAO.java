package it.unipi.largescale.pixelindex.dao;

import it.unipi.largescale.pixelindex.dto.GamePreviewDTO;
import it.unipi.largescale.pixelindex.exceptions.DAOException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.exceptions.ServiceUnavailableException;

import static org.neo4j.driver.Values.parameters;

public class GameNeo4jDAO extends BaseNeo4jDAO {

    public void insertGame(String gameId) throws DAOException {
        try (Driver neoDriver = beginConnection()) {
            String query = "CREATE (g:Game {mongoId: $id})";
            Map<String, Object> params = new HashMap<>();
            params.put("id", gameId);

            try (Session session = neoDriver.session()) {
                session.executeWrite(tx -> {
                    tx.run(query, params);
                    return null;
                });
            }
        } catch (Exception ex) {
            throw new DAOException(ex);
        }
    }

    public List<GamePreviewDTO> getGamesByName(String name) throws DAOException {
        ArrayList<GamePreviewDTO> games;
        String lowerCasePar = name.toLowerCase();
        try (Driver neoDriver = BaseNeo4jDAO.beginConnection();
             Session session = neoDriver.session()) {

            games = session.executeRead(tx -> {
                Result result = tx.run("MATCH (matchingGame:Game) " +
                                "WHERE toLower(matchingGame.name) CONTAINS $lowerCasePar " +
                                "RETURN matchingGame.mongoId AS id, " +
                                "matchingGame.name AS name, " +
                                "matchingGame.releaseDate AS releaseDate " +
                                "LIMIT 20",
                        parameters("lowerCasePar", lowerCasePar));

                ArrayList<GamePreviewDTO> gamePreviewDTOArrayList = new ArrayList<>();
                while (result.hasNext()) {
                    Record record = result.next();
                    GamePreviewDTO gamePreviewDTO = new GamePreviewDTO();
                    gamePreviewDTO.setId(record.get("id").asString());
                    gamePreviewDTO.setName(record.get("name").asString());
                    LocalDate date = LocalDate.parse(record.get("releaseDate").asString());
                    gamePreviewDTO.setReleaseDate(date);
                    gamePreviewDTOArrayList.add(gamePreviewDTO);
                }
                return gamePreviewDTOArrayList;
            });
        } catch (ServiceUnavailableException ex) {
            throw new DAOException("Cannot reach Neo4j Server");
        }
        return games;
    }
}
