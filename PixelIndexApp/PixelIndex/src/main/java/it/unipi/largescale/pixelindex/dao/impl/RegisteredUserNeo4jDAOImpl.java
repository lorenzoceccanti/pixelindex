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
}