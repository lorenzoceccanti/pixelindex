package it.unipi.largescale.pixelindex.dao.neo4j;

import it.unipi.largescale.pixelindex.dto.TrendingGamesDTO;
import it.unipi.largescale.pixelindex.exceptions.DAOException;

import it.unipi.largescale.pixelindex.model.Game;
import org.neo4j.driver.Session;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Value;

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
        String startDate = year + "-01-01";
        String endDate = year + "-12-31";

        try (Driver neoDriver = beginConnection()) {
            String query = """
                    MATCH (g:Game)<-[r:ADDS_TO_LIBRARY]-(u:User)
                    WHERE r.date >= DATE($startDate) AND r.date <= DATE($endDate)
                    WITH g, COUNT(r) AS count
                    ORDER BY count DESC
                    LIMIT $limit
                    RETURN g.name AS name, count
                    """;
            Value parameters = parameters("startDate", startDate, "endDate", endDate, "limit", limit);

            try (Session session = neoDriver.session()) {
                return session.executeRead(tx -> tx.run(query, parameters)
                        .list(record -> {
                            TrendingGamesDTO game = new TrendingGamesDTO();
                            game.setGameName(record.get("name").asString());
                            game.setCount(record.get("count").asInt());
                            return game;
                        }));
            }
        } catch (Exception ex) {
            throw new DAOException(ex);
        }
    }
}
