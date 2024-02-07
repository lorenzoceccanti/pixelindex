package it.unipi.largescale.pixelindex.dao.neo4j;

import it.unipi.largescale.pixelindex.dto.UserSearchDTO;
import it.unipi.largescale.pixelindex.exceptions.DAOException;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.exceptions.ServiceUnavailableException;

import java.util.ArrayList;

import static org.neo4j.driver.Values.parameters;

public class RegisteredUserNeo4jDAO {
    public void register(String username) throws DAOException {
        try (Driver neoDriver = BaseNeo4jDAO.beginConnection();
             Session session = neoDriver.session()) {
            session.executeWrite(tx -> {
                tx.run("MERGE (u:User {username: $username})",
                        parameters("username", username));
                return null;
            });
        } catch (ServiceUnavailableException ex) {
            throw new DAOException("Cannot reach Neo4j Server");
        }
    }


    /**
     * This method will trigger a FOLLOWS relationship
     *
     * @param usernameSrc The username that press on follow
     * @param usernameDst The username followed
     * @throws DAOException
     */
    public void followUser(String usernameSrc, String usernameDst) throws DAOException {
        /* It does not matter to worry about the user existence,
        since this method is called after the search*/
        try (Driver neoDriver = BaseNeo4jDAO.beginConnection();
             Session session = neoDriver.session()) {
            session.executeWrite(tx -> {
                tx.run("MATCH (src: User) WHERE src.username = $usernameSrc " +
                                "MATCH (dst: User) WHERE dst.username = $usernameDst " +
                                "MERGE (src)-[:FOLLOWS]->(dst);",
                        parameters("usernameSrc", usernameSrc, "usernameDst", usernameDst));
                return null;
            });
        } catch (ServiceUnavailableException ex) {
            throw new DAOException("Cannot reach Neo4j Server");
        }
    }

    public void unfollowUser(String usernameSrc, String usernameDst) throws DAOException {
        try (Driver neoDriver = BaseNeo4jDAO.beginConnection();
             Session session = neoDriver.session()) {
            session.executeWrite(tx -> {
                tx.run("MATCH (src:User {username:$usernameSrc})-[r:FOLLOWS]->(dst:User{username:$usernameDst}) " +
                        "DELETE r;", parameters("usernameSrc", usernameSrc, "usernameDst", usernameDst));
                return null;
            });
        } catch (ServiceUnavailableException ex) {
            throw new DAOException("Cannot reach Neo4j Server");
        }
    }

    /** Drops an user with all the involved relationships from the graph
     *
     * @param username The username to be removed
     * @throws DAOException
     */
    public void deleteUser(String username) throws DAOException{
        try(Driver neoDriver = BaseNeo4jDAO.beginConnection();
            Session session = neoDriver.session()){
            session.executeWrite(tx -> {
                tx.run("MATCH (u:User {username: $username})-[w:WRITES]->(re:Review) " +
                        "DETACH DELETE u, re;", parameters("username", username));
                return null;
            });
        }catch (ServiceUnavailableException ex) {
            throw new DAOException("Cannot reach Neo4j Server");
        }
    }
}