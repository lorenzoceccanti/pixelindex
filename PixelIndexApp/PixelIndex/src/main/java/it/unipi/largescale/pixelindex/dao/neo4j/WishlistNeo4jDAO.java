package it.unipi.largescale.pixelindex.dao.neo4j;

import it.unipi.largescale.pixelindex.dto.GamePreviewDTO;
import it.unipi.largescale.pixelindex.exceptions.DAOException;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.exceptions.ServiceUnavailableException;

import java.util.ArrayList;

import static org.neo4j.driver.Values.parameters;

public class WishlistNeo4jDAO {
    public void insertGame(String userId, String gameId) throws DAOException {
        // Adding a relationship between User and Game of type ADDS_TO_WHISHLIST
        try (Driver neoDriver = BaseNeo4jDAO.beginConnection();
             Session session = neoDriver.session()) {
            session.executeWrite(tx -> {
                tx.run("MATCH (src: User) WHERE src.username = $usernameSrc " +
                                "MATCH (dst: Game) WHERE dst.mongoId =  $gameIdDst " +
                                "MERGE (src)-[:ADDS_TO_WISHLIST]->(dst);",
                        parameters("usernameSrc", userId, "gameIdDst", gameId));
                return null;
            });
        } catch (ServiceUnavailableException ex) {
            throw new DAOException("Cannot reach Neo4j Server");
        }
    }

    public void removeGame(String userId, String gameId) throws DAOException {
        // Removing a relationship of type ADDS_TO_WHISHLIST
        try (Driver neoDriver = BaseNeo4jDAO.beginConnection();
             Session session = neoDriver.session()) {
            session.executeWrite(tx -> {
                tx.run("MATCH (src:User{username:$usernameSrc})-[r:ADDS_TO_WISHLIST]->(dst:Game{mongoId:$mongoIdDst}) " +
                        "DELETE r;", parameters("usernameSrc", userId, "mongoIdDst", gameId));
                return null;
            });
        } catch (ServiceUnavailableException ex) {
            throw new DAOException("Cannot reach Neo4j Server");
        }
    }

    public ArrayList<GamePreviewDTO> getGames(String username, int page) throws DAOException {
        ArrayList<GamePreviewDTO> returnObject;
        try (Driver neoDriver = BaseNeo4jDAO.beginConnection();
             Session session = neoDriver.session()) {
            returnObject = session.executeRead(tx -> {
                Result result = tx.run(
                        """
                                MATCH(u:User{username:$username})-[:ADDS_TO_WISHLIST]->(g:Game)
                                RETURN g.mongoId, g.name AS name, g.releaseYear as releaseYear
                                ORDER BY g.name DESC
                                SKIP $page * 10
                                LIMIT 10;
                                """,
                        parameters("username", username, "page", page));
                ArrayList<GamePreviewDTO> gamePreviewDTOs = new ArrayList<>();
                while (result.hasNext()) {
                    Record r = result.next();
                    GamePreviewDTO gamePreviewDTO = new GamePreviewDTO();
                    gamePreviewDTO.setId(r.get("mongoId").asString());
                    gamePreviewDTO.setName(r.get("name").asString());
                    if (r.get("releaseYear").isNull()) {
                        gamePreviewDTO.setReleaseYear(null);
                    } else {
                        gamePreviewDTO.setReleaseYear(r.get("releaseYear").asInt());
                    }
                    gamePreviewDTOs.add(gamePreviewDTO);
                }
                return gamePreviewDTOs;
            });
        } catch (ServiceUnavailableException ex) {
            throw new DAOException("Cannot reach Neo4j Server");
        }
        return returnObject;
    }
}
