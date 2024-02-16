package it.unipi.largescale.pixelindex.dao.neo4j;

import it.unipi.largescale.pixelindex.dto.GameSuggestionDTO;
import it.unipi.largescale.pixelindex.exceptions.DAOException;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Result;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;

import java.util.ArrayList;
import java.util.List;

import static org.neo4j.driver.Values.parameters;

public class SuggestionsNeo4jDAO extends BaseNeo4jDAO {

    public List<GameSuggestionDTO> getSuggestedGames(String username) throws DAOException {
        ArrayList<GameSuggestionDTO> suggestedGames;
        try (Driver neoDriver = BaseNeo4jDAO.beginConnection();
             Session session = neoDriver.session()) {
            suggestedGames = session.executeRead(tx -> {
                Result result = tx.run("MATCH (u1:User {username:$username})-[f:FOLLOWS]->(u2:User)" +
                                "MATCH (u2:User)-[r:ADDS_TO_LIBRARY]->(g:Game) " +
                                "WHERE NOT EXISTS ((u1)-[:ADDS_TO_LIBRARY]->(g)) " +
                                "RETURN g.name AS name, COUNT(f) AS connectionsNumber " +
                                "ORDER BY connectionsNumber DESC " +
                                "LIMIT 10",
                        parameters("username", username));
                ArrayList<GameSuggestionDTO> games = new ArrayList<>();
                while (result.hasNext()) {
                    Record record = result.next();
                    GameSuggestionDTO gameSuggestionDTO = new GameSuggestionDTO();
                    gameSuggestionDTO.setGameName(record.get("name").asString());
                    gameSuggestionDTO.setConnectionsNumber(record.get("connectionsNumber").asInt());
                    games.add(gameSuggestionDTO);
                }
                return games;
            });
        } catch (Exception ex) {
            throw new DAOException(ex);
        }
        return suggestedGames;
    }

    public ArrayList<String> getSuggestedUsers(String username) throws DAOException {
        ArrayList<String> suggestedUsers;
        try (Driver neoDriver = BaseNeo4jDAO.beginConnection();
             Session session = neoDriver.session()) {
            suggestedUsers = session.executeRead(tx -> {
                Result result = tx.run("MATCH (u1:User {username:$username})-[:ADDS_TO_LIBRARY]->(g: Game)" +
                                "<-[r:ADDS_TO_LIBRARY]-(u2: User) " +
                                "WHERE u1.username <> u2.username AND NOT ((u1)-[:FOLLOWS]->(u2)) " +
                                "RETURN u2.username AS username, COUNT(g) AS NumberOfMutualGames " +
                                "ORDER BY NumberOfMutualGames DESC " +
                                "LIMIT 10",
                        parameters("username", username));
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
