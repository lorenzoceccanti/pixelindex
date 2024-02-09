package it.unipi.largescale.pixelindex.dao.neo4j;

import it.unipi.largescale.pixelindex.dto.GameLibraryElementDTO;
import it.unipi.largescale.pixelindex.exceptions.DAOException;
import it.unipi.largescale.pixelindex.utils.Utils;
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
                tx.run("MATCH (u: User)-[r:ADDS_TO_LIBRARY]->(g: Game) " +
                                "WHERE u.username = $username AND g.mongoId = $gameId " +
                                "DELETE r;",
                        parameters("username", username, "gameId", gameId));
                return null;
            });
        } catch (ServiceUnavailableException ex) {
            throw new DAOException("Cannot reach Neo4j Server");
        }
    }

    public List<GameLibraryElementDTO> getGames(String username, Integer page) throws DAOException {
        ArrayList<GameLibraryElementDTO> games;
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

                ArrayList<GameLibraryElementDTO> gamePreviewDTOArrayList = new ArrayList<>();
                while (result.hasNext()) {
                    Record record = result.next();
                    GameLibraryElementDTO gameLibraryElementDTO = new GameLibraryElementDTO();
                    gameLibraryElementDTO.setId(record.get("id").asString());
                    gameLibraryElementDTO.setName(record.get("name").asString());
                    if (record.get("releaseYear").isNull()) {
                        gameLibraryElementDTO.setReleaseYear(null);
                    } else {
                        gameLibraryElementDTO.setReleaseYear(record.get("releaseYear").asInt());
                    }
                    gameLibraryElementDTO.setAddedDate(Utils.convertStringToLocalDate(record.get("addedDate").asString()));
                    gamePreviewDTOArrayList.add(gameLibraryElementDTO);
                }
                return gamePreviewDTOArrayList;
            });
        } catch (ServiceUnavailableException ex) {
            throw new DAOException("Cannot reach Neo4j Server");
        }
        return games;
    }
}
