package it.unipi.largescale.pixelindex.dao.neo4j;

import it.unipi.largescale.pixelindex.dto.GameLibraryElementDTO;
import it.unipi.largescale.pixelindex.exceptions.DAOException;
import it.unipi.largescale.pixelindex.model.Game;
import it.unipi.largescale.pixelindex.model.Library;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Record;
import org.neo4j.driver.exceptions.ServiceUnavailableException;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

import static org.neo4j.driver.Values.parameters;

public class LibraryNeo4jDAO extends BaseNeo4jDAO {
    public void addGame(String username, String gameId) throws DAOException {
        try (Driver neoDriver = BaseNeo4jDAO.beginConnection();
             Session session = neoDriver.session()) {
            session.executeWrite(tx -> {
                tx.run("MATCH (u: User) WHERE u.username = $username " +
                                "MATCH (g: Game) WHERE g.mongoId = $gameId " +
                                "MERGE (u)-[r:ADDS_TO_LIBRARY]->(g) " +
                                "SET r.date = date();",
                        parameters("username", username, "gameId", gameId));
                return null;
            });
        } catch (ServiceUnavailableException ex) {
            throw new DAOException("Cannot reach Neo4j Server");
        }
    }

    public void removeGame(String username, String gameId) throws DAOException {
        try (Driver neoDriver = BaseNeo4jDAO.beginConnection();
             Session session = neoDriver.session()) {
            session.executeWrite(tx -> {
                tx.run("MATCH (u: User)-[r:ADDS_TO_LIBRARY]->(g: Game) " +
                                "WHERE u.username = $username AND g.mongoId = $gameId " +
                                "DELETE r;",
                        parameters("username", username, "gameId", gameId));
                return null;
            });
        } catch (ServiceUnavailableException ex) {
            throw new DAOException("Cannot reach Neo4j Server");
        }
    }

    public List<GameLibraryElementDTO> getGames(String username, Integer page) throws DAOException {
        Library library = new Library();
        try (Driver neoDriver = BaseNeo4jDAO.beginConnection();
             Session session = neoDriver.session()) {
             session.executeRead(tx -> {
                Result result = tx.run(
                        """
                                MATCH (u:User {username: $username})-[a:ADDS_TO_LIBRARY]->(g:Game)
                                RETURN g.mongoId AS id, g.name AS name, g.releaseYear AS releaseYear, a.date AS addedDate
                                ORDER BY addedDate DESC
                                SKIP 10 * $page
                                LIMIT 10
                                """,
                        parameters("username", username, "page", page));

                while (result.hasNext()) {
                    Record record = result.next();
                    Game game = new Game();
                    game.setId(record.get("id").asString());
                    game.setName(record.get("name").asString());

                    LocalDate addedDate = record.get("addedDate").asLocalDate();
                    Integer releaseYear = record.get("releaseYear").isNull() ? null : record.get("releaseYear").asInt();
                    library.addEntry(new Library.LibraryEntry(game, addedDate, releaseYear));

                }
                return null;
            });
        } catch (ServiceUnavailableException ex) {
            throw new DAOException("Cannot reach Neo4j Server");
        }

        List<GameLibraryElementDTO> dtos = new ArrayList<>();
        for (Library.LibraryEntry entry : library.getEntries()) {
            GameLibraryElementDTO dto = new GameLibraryElementDTO();
            dto.setId(entry.game().getId());
            dto.setName(entry.game().getName());
            dto.setReleaseYear(entry.releaseYear());
            dto.setAddedDate(entry.addedDate());
            dtos.add(dto);
        }
        return dtos;
    }
}
