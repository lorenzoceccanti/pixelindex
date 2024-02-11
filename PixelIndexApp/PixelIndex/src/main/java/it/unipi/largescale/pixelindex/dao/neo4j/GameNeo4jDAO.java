package it.unipi.largescale.pixelindex.dao.neo4j;

import it.unipi.largescale.pixelindex.dto.TrendingGamesDTO;
import it.unipi.largescale.pixelindex.exceptions.DAOException;

import it.unipi.largescale.pixelindex.model.Game;
import org.neo4j.driver.Session;
import org.neo4j.driver.Driver;

import java.util.List;

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

    public List<TrendingGamesDTO> getMostAddedToLibraryGames(Integer year, Integer limit) throws DAOException {
        try (Driver neoDriver = beginConnection()) {
            String query = """
                    MATCH (g:Game)
                    WHERE g.releaseYear = $year
                    RETURN g.mongoId AS id, g.name AS name, g.releaseYear AS releaseYear
                    ORDER BY g.mongoId
                    LIMIT $limit
                    """;
            try (Session session = neoDriver.session()) {
                return session.executeRead(tx -> {
                    return tx.run(query, parameters("year", year, "limit", limit))
                            .list(record -> {
                                TrendingGamesDTO game = new TrendingGamesDTO();
                                game.setGameName(record.get("name").asString());
                                game.setCount(record.get("releaseYear").asInt());
                                return game;
                            });
                });
            }
        } catch (Exception ex) {
            throw new DAOException(ex);
        }
    }
}
