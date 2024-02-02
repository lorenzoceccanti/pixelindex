package it.unipi.largescale.pixelindex.dao.neo4j;

import it.unipi.largescale.pixelindex.dto.GamePreviewDTO;
import it.unipi.largescale.pixelindex.exceptions.DAOException;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Record;
import org.neo4j.driver.exceptions.ServiceUnavailableException;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

import static org.neo4j.driver.Values.parameters;

public class LibraryNeo4jDAO extends BaseNeo4jDAO {
    public void addGame(String username, String gameId) throws DAOException {
        try (Driver neoDriver = BaseNeo4jDAO.beginConnection();
             Session session = neoDriver.session()) {
            session.executeWrite(tx -> {
                tx.run("MATCH (u: User) WHERE u.username = $username" +
                                "MATCH (g: Game) WHERE g.mongoId = $gameId" +
                                "MERGE (u)-[r:ADDS_TO_LIBRARY {date]->(g)" +
                                "SET r.date = date().year + '-' + apoc.text.lpad(date().month, 2, '0') + '-' + apoc.text.lpad(date().day, 2, '0');",
                        parameters("$username", username, "$gameId", gameId));
                return null;
            });
        } catch (ServiceUnavailableException ex) {
            throw new DAOException("Cannot reach Neo4j Server");
        }
    }

    public void removeGame(String username, String gameId) throws DAOException {
        try (Driver neoDriver = BaseNeo4jDAO.beginConnection();
             Session session = neoDriver.session()) {
            session.executeWrite(tx -> {
                tx.run("MATCH (u: User)-[r:ADDS_TO_LIBRARY]->(g: Game)" +
                                "WHERE u.username = $username AND g.mongoId = $gameId" +
                                "DELETE (u)-[:ADDS_TO_LIBRARY]->(g);)",
                        parameters("$username", username, "$gameId", gameId));
                return null;
            });
        } catch (ServiceUnavailableException ex) {
            throw new DAOException("Cannot reach Neo4j Server");
        }
    }

    public List<GamePreviewDTO> getGames(String username) throws DAOException {
        ArrayList<GamePreviewDTO> games;
        try (Driver neoDriver = BaseNeo4jDAO.beginConnection();
             Session session = neoDriver.session()) {
            games = session.executeRead(tx -> {
                Result result = tx.run("MATCH (u:User {username: $username})-[:ADDS_TO_LIBRARY]->(g:Game) " +
                                "RETURN g.mongoId AS id, g.name AS name, g.releaseDate AS releaseDate",
                        parameters("username", username));

                ArrayList<GamePreviewDTO> gamePreviewDTOArrayList = new ArrayList<>();
                while (result.hasNext()) {
                    Record record = result.next();
                    GamePreviewDTO gamePreviewDTO = new GamePreviewDTO();
                    gamePreviewDTO.setId(record.get("id").asString());
                    gamePreviewDTO.setName(record.get("name").asString());
                    LocalDate date = LocalDate.parse(record.get("releaseDate").asString());
                    gamePreviewDTO.setReleaseYear(date.getYear());
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
