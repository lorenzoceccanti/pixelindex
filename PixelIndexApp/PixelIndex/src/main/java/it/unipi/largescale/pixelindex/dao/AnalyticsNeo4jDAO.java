package it.unipi.largescale.pixelindex.dao;

import it.unipi.largescale.pixelindex.dto.GameDTO;
import it.unipi.largescale.pixelindex.exceptions.DAOException;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Result;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.neo4j.driver.Values.parameters;

public class AnalyticsNeo4jDAO extends BaseNeo4jDAO {

    public List<GameDTO> getSuggestedGames(String username) throws DAOException {
        ArrayList<GameDTO> suggestedGames;
        try (Driver neoDriver = BaseNeo4jDAO.beginConnection();
             Session session = neoDriver.session()) {
            suggestedGames = session.executeRead(tx -> {
                Result result = tx.run("MATCH (u1:User {username:$username})-[:FOLLOWS]->(u2:User)" +
                                "MATCH (u2:User)-[r:ADDS_TO_LIBRARY]->(g:Game) " +
                                "WHERE NOT EXISTS ((u1)-[:ADDS_TO_LIBRARY]->(g)) " +
                                "RETURN g1, COUNT(r) AS NumberAdded " +
                                "ORDER BY NumberAdded DESC " +
                                "LIMIT 10",
                        parameters("$username", username));
                ArrayList<GameDTO> games = new ArrayList<>();
                while (result.hasNext()) {
                    Record record = result.next();
                    GameDTO gameDTO = new GameDTO();
                    gameDTO.setId(record.get("id").asString());
                    gameDTO.setName(record.get("name").asString());
                    gameDTO.setReleaseDate(LocalDate.parse(record.get("releaseDate").asString()));
                    games.add(gameDTO);
                }
                return games;
            });
        } catch (Exception ex) {
            throw new DAOException(ex);
        }
        return suggestedGames;
    }

    public List<String> getSuggestedUsers(String username) throws DAOException {
        ArrayList<String> suggestedUsers;
        try (Driver neoDriver = BaseNeo4jDAO.beginConnection();
             Session session = neoDriver.session()) {
            suggestedUsers = session.executeRead(tx -> {
                Result result = tx.run("MATCH (u1:User {username:$username})-[:ADDS_TO_LIBRARY]->(g: Game)"+
                                "<-[r:ADDS_TO_LIBRARY]-(u2: User) " +
                                "WHERE u1 <> u2 AND NOT ((u1)-[:FOLLOWS]->(u2)) " +
                                "RETURN u2.username, COUNT(g) AS NumberOfMutualGames " +
                                "ORDER BY NumberOfMutualGames DESC " +
                                "LIMIT 10",
                        parameters("$username", username));
                ArrayList<String> users = new ArrayList<>();
                while (result.hasNext()) {
                    Record record = result.next();
                    users.add(record.get("username").asString());
                }
                return users;
            });
        } catch (Exception ex) {
            throw new DAOException(ex);
        }
        return suggestedUsers;
    }
}
