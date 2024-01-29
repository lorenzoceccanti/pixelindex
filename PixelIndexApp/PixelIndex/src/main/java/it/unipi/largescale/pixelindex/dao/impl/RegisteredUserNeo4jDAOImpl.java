package it.unipi.largescale.pixelindex.dao.impl;

import it.unipi.largescale.pixelindex.dao.RegisteredUserNeo4jDAO;
import it.unipi.largescale.pixelindex.exceptions.DAOException;
import it.unipi.largescale.pixelindex.model.RegisteredUser;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.exceptions.ServiceUnavailableException;

import static org.neo4j.driver.Values.parameters;

public class RegisteredUserNeo4jDAOImpl implements RegisteredUserNeo4jDAO {
    @Override
    public void register(String username) throws DAOException {
        try(Driver neoDriver = BaseNeo4jDAO.beginConnection();
        Session session = neoDriver.session())
        {
            session.executeWrite(tx->{
                tx.run("MERGE (u:User {username: $username})",
                        parameters("username",username));
                return null;
            });
        }catch(ServiceUnavailableException ex)
        {
            System.out.println("Cannot reach Neo4j Server");
        }
    }

    /**
     * This method will trigger a FOLLOWS relationship
     * @param usernameSrc The username that press on follow
     * @param usernameDst The username followed
     * @throws DAOException
     */
    public void follow(String usernameSrc, String usernameDst) throws DAOException{
        /* It does not matter to worry about the user existence,
        since this method is called after the search*/
        try(Driver neoDriver = BaseNeo4jDAO.beginConnection();
            Session session = neoDriver.session())
        {
            session.executeWrite(tx -> {
                tx.run("MATCH (src: User) WHERE src.username = $usernameSrc" +
                        "MATCH (dst: User) WHERE dst.username = $usernameDst" +
                        "CREATE (src)-[:FOLLOWS]->(dst);)",
                parameters("usernameSrc", usernameSrc, "usernameDst", usernameDst));
                return null;
            });
        }catch(ServiceUnavailableException ex)
        {
            System.out.println("Cannot reach Neo4j Server");
        }
    }
}