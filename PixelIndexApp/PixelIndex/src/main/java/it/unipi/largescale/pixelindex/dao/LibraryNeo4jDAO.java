package it.unipi.largescale.pixelindex.dao;

import it.unipi.largescale.pixelindex.exceptions.DAOException;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.exceptions.ServiceUnavailableException;

import static org.neo4j.driver.Values.parameters;

public class LibraryNeo4jDAO extends BaseNeo4jDAO {
    public void insertAddsToLibrary(String username, String gameId) throws DAOException {
        try(Driver neoDriver = BaseNeo4jDAO.beginConnection();
            Session session = neoDriver.session())
        {
            session.executeWrite(tx -> {
                tx.run("MATCH (u: User) WHERE u.username = $username" +
                                "MATCH (g: Game) WHERE g.mongoId = $gameId" +
                                "MERGE (u)-[r:ADDS_TO_LIBRARY {date]->(g)" +
                                "SET r.date = date().year + '-' + apoc.text.lpad(date().month, 2, '0') + '-' + apoc.text.lpad(date().day, 2, '0');",
                        parameters("$username", username, "$gameId", gameId));
                return null;
            });
        }catch(ServiceUnavailableException ex)
        {
            throw new DAOException("Cannot reach Neo4j Server");
        }
    }
    public void deleteAddsToLibrary(String username, String gameId) throws DAOException {
        try(Driver neoDriver = BaseNeo4jDAO.beginConnection();
            Session session = neoDriver.session())
        {
            session.executeWrite(tx -> {
                tx.run("MATCH (u: User)-[r:ADDS_TO_LIBRARY]->(g: Game)" +
                                "WHERE u.username = $username AND g.mongoId = $gameId" +
                                "DELETE (u)-[:ADDS_TO_LIBRARY]->(g);)",
                        parameters("$username", username, "$gameId", gameId));
                return null;
            });
        }catch(ServiceUnavailableException ex)
        {
            throw new DAOException("Cannot reach Neo4j Server");
        }
    }
}
