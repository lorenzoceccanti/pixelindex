package it.unipi.largescale.pixelindex.service.impl;

import it.unipi.largescale.pixelindex.dao.mongo.RegisteredUserMongoDAO;
import it.unipi.largescale.pixelindex.dao.neo4j.RegisteredUserNeo4jDAO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;
import it.unipi.largescale.pixelindex.exceptions.DAOException;
import it.unipi.largescale.pixelindex.service.ModeratorService;

public class ModeratorServiceImpl implements ModeratorService {
    private RegisteredUserMongoDAO registeredUserMongo;
    private RegisteredUserNeo4jDAO registeredUserNeo;

    public ModeratorServiceImpl()
    {
        this.registeredUserMongo = new RegisteredUserMongoDAO();
        this.registeredUserNeo = new RegisteredUserNeo4jDAO();
    }
    /**
     *
     * @param username The username to be removed
     */
    public void banUser(String username) throws ConnectionException {
        try{
            registeredUserMongo.banUser(username);
        }catch(DAOException ex)
        {
            throw new ConnectionException(ex);
        }
    }

    public void deleteUserFromGraph(String username) throws ConnectionException{
        try{
            registeredUserNeo.deleteUser(username);
        }catch(DAOException ex)
        {
            throw new ConnectionException(ex);
        }
    }
}
