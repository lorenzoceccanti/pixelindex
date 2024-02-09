package it.unipi.largescale.pixelindex.dao.neo4j;

import it.unipi.largescale.pixelindex.dto.GamePreviewDTO;
import it.unipi.largescale.pixelindex.dto.UserLibraryDTO;
import it.unipi.largescale.pixelindex.exceptions.DAOException;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Record;
import org.neo4j.driver.exceptions.ServiceUnavailableException;

import java.util.List;
import java.util.ArrayList;

import static org.neo4j.driver.Values.parameters;

public class LibraryNeo4jDAO extends BaseNeo4jDAO {
    public void addGame(String username, String gameId) throws DAOException {
        try (Driver neoDriver = BaseNeo4jDAO.beginConnection();
             Session session = neoDriver.session()) {
            session.executeWrite(tx -> {
                tx.run("MATCH (u: User) WHERE u.username = $username " +
                                "MATCH (g: Game) WHERE g.mongoId = $gameId " +
                                "MERGE (u)-[r:ADDS_TO_LIBRARY]->(g) " +
                                "SET r.date = toString(date().year) + '-' + " +
                                "apoc.text.lpad(toString(date().month), 2, '0') + '-' + " +
                                "apoc.text.lpad(toString(date().day), 2, '0');",
                        parameters("username", username, "gameId", gameId));
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
                        parameters("username", username, "gameId", gameId));
                return null;
            });
        } catch (ServiceUnavailableException ex) {
            throw new DAOException("Cannot reach Neo4j Server");
        }
    }

    public List<UserLibraryDTO> getGames(String username, Integer page) throws DAOException {
        ArrayList<UserLibraryDTO> games;
        try (Driver neoDriver = BaseNeo4jDAO.beginConnection();
             Session session = neoDriver.session()) {
            games = session.executeRead(tx -> {
                Result result = tx.run(
                        """
                                MATCH (u:User {username: $username})-[a:ADDS_TO_LIBRARY]->(g:Game)
                                RETURN g.mongoId AS id, g.name AS name, g.releaseYear AS releaseYear, a.date AS addedDate
                                ORDER BY addedDate DESC
                                SKIP 10 * $page
                                LIMIT 10
                                """,
                        parameters("username", username, "page", page));

                ArrayList<UserLibraryDTO> gamePreviewDTOArrayList = new ArrayList<>();
                while (result.hasNext()) {
                    Record record = result.next();
                    UserLibraryDTO userLibraryDTO = new UserLibraryDTO();
                    userLibraryDTO.setId(record.get("id").asString());
                    userLibraryDTO.setName(record.get("name").asString());
                    if (record.get("releaseYear").isNull()) {
                        userLibraryDTO.setReleaseYear(null);
                    } else {
                        userLibraryDTO.setReleaseYear(record.get("releaseYear").asInt());
                    }
                    userLibraryDTO.setAddedDate(record.get("addedDate").asLocalDate());
                    gamePreviewDTOArrayList.add(userLibraryDTO);
                }
                return gamePreviewDTOArrayList;
            });
        } catch (ServiceUnavailableException ex) {
            throw new DAOException("Cannot reach Neo4j Server");
        }
        return games;
    }
}
