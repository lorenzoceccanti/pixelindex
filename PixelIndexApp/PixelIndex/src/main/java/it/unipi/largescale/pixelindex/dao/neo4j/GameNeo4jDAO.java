package it.unipi.largescale.pixelindex.dao.neo4j;

import it.unipi.largescale.pixelindex.exceptions.DAOException;

import it.unipi.largescale.pixelindex.model.Game;
import org.neo4j.driver.Session;
import org.neo4j.driver.Driver;

import static org.neo4j.driver.Values.parameters;

public class GameNeo4jDAO extends BaseNeo4jDAO {

    public void insertGame(Game game) throws DAOException {
        try (Driver neoDriver = beginConnection()) {
            String query = "CREATE (g:Game {mongoId: $id, name: $name, releaseYear: $releaseYear})";
            try (Session session = neoDriver.session()) {
                session.executeWrite(tx -> {
                    tx.run(query, parameters("id", game.getId(),
                            "name", game.getName(),
                            "releaseYear", game.getReleaseDate().getYear()));
                    return null;
                });
            }
        } catch (Exception ex) {
            throw new DAOException(ex);
        }
    }
}
