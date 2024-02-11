package it.unipi.largescale.pixelindex.service.impl;

import it.unipi.largescale.pixelindex.controller.ConsistencyThread;
import it.unipi.largescale.pixelindex.dao.mongo.GameMongoDAO;
import it.unipi.largescale.pixelindex.dao.mongo.RegisteredUserMongoDAO;
import it.unipi.largescale.pixelindex.dao.neo4j.GameNeo4jDAO;
import it.unipi.largescale.pixelindex.dao.neo4j.RegisteredUserNeo4jDAO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;
import it.unipi.largescale.pixelindex.exceptions.DAOException;
import it.unipi.largescale.pixelindex.model.Game;
import it.unipi.largescale.pixelindex.service.ModeratorService;

import java.util.ArrayList;

public class ModeratorServiceImpl implements ModeratorService {
    private final RegisteredUserMongoDAO registeredUserMongo;
    private final RegisteredUserNeo4jDAO registeredUserNeo;
    private final GameMongoDAO gameMongoDAO;
    private final GameNeo4jDAO gameNeo4jDAO;


    public ModeratorServiceImpl()
    {
        this.registeredUserMongo = new RegisteredUserMongoDAO();
        this.registeredUserNeo = new RegisteredUserNeo4jDAO();
        this.gameMongoDAO = new GameMongoDAO();
        this.gameNeo4jDAO = new GameNeo4jDAO();
    }
    /**
     *
     * @param username The username to be removed
     */

    @Override
    public void banUser(String username, ConsistencyThread consistencyThread) throws ConnectionException {
        try{
            registeredUserMongo.banUser(username);
            consistencyThread.addTask(() -> {
                try {
                    registeredUserNeo.deleteUser(username);
                } catch (DAOException e) {
                }
            });
        }catch(DAOException ex)
        {
            throw new ConnectionException(ex);
        }
    }

    @Override
    public void synchronizeGames(ConsistencyThread consistencyThread) throws ConnectionException {
        try {
            ArrayList<Game> inconsistentGames = gameMongoDAO.getInconsistentGames();
            for(Game game : inconsistentGames)
            {
                consistencyThread.addTask(() -> {
                    try {
                        gameNeo4jDAO.insertGame(game);
                        gameMongoDAO.updateConsistencyFlag(game.getId());
                    } catch (DAOException e) {
                    }
                });
            }
        } catch (DAOException ex) {
            throw new ConnectionException(ex);
        }
    }
}
