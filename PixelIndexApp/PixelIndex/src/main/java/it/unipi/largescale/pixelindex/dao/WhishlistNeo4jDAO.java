package it.unipi.largescale.pixelindex.dao;

import it.unipi.largescale.pixelindex.dto.GamePreviewDTO;
import it.unipi.largescale.pixelindex.exceptions.DAOException;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.exceptions.ServiceUnavailableException;

import static org.neo4j.driver.Values.parameters;

public class WhishlistNeo4jDAO {
    public void insertGame(String userId, String gameId) throws DAOException
    {
        // Adding a relationship between User and Game of type ADDS_TO_WHISHLIST
        try(Driver neoDriver = BaseNeo4jDAO.beginConnection();
            Session session = neoDriver.session())
        {
            session.executeWrite(tx -> {
                tx.run("MATCH (src: User) WHERE src.username = $usernameSrc " +
                                "MATCH (dst: Game) WHERE dst.mongoId =  $gameIdDst " +
                                "MERGE (src)-[:ADDS_TO_WHISHLIST]->(dst);",
                        parameters("usernameSrc", userId, "gameIdDst",gameId));
                return null;
            });
        }catch(ServiceUnavailableException ex)
        {
            throw new DAOException("Cannot reach Neo4j Server");
        }
    }

    public void removeGame(String userId, String gameId) throws DAOException {
        // Removing a relationship of type ADDS_TO_WHISHLIST
        try(Driver neoDriver = BaseNeo4jDAO.beginConnection();
            Session session = neoDriver.session())
        {
            session.executeWrite(tx -> {
                tx.run("MATCH (src:User{username:$usernameSrc})-[r:ADDS_TO_WHISHLIST]->(dst:Game{mongoId:$mongoIdDst}) " +
                        "DELETE r;", parameters("usernameSrc",userId, "mongoIdDst", gameId));
                return null;
            });
        }catch(ServiceUnavailableException ex)
        {
            throw new DAOException("Cannot reach Neo4j Server");
        }
    }
}
