package it.unipi.largescale.pixelindex.dao.impl;

import it.unipi.largescale.pixelindex.dao.GameNeo4jDAO;
import it.unipi.largescale.pixelindex.exceptions.DAOException;

import java.util.HashMap;
import java.util.Map;
import org.neo4j.driver.Session;
import org.neo4j.driver.Driver;

public class GameNeo4jDAOImpl extends BaseNeo4jDAO implements GameNeo4jDAO {

    @Override
    public void insertGame(String gameId) throws DAOException {
        try(Driver neoDriver = beginConnection()) {
            String query = "CREATE (g:Game {mongoId: $id})";
            Map<String, Object> params = new HashMap<>();
            params.put("id", gameId);

            try (Session session = neoDriver.session()) {
                session.executeWrite(tx -> {
                    tx.run(query, params);
                    return null;
                });
            }
        } catch (Exception ex) {
            throw new DAOException(ex);
        }
    }
}
